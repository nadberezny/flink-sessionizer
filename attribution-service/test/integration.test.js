const { getFunction } = require('@google-cloud/functions-framework/testing');

// Import the function
require('../index');

// Get the HTTP function
const attribution = getFunction('attribution');

describe('Attribution Service Integration Tests', () => {
  // Test the health check endpoint
  test('GET / should return 200 and a health message', async () => {
    // Create a mock request object
    const req = {
      method: 'GET',
      path: '/',
      headers: {},
      query: {},
      body: {}
    };

    // Create a mock response object
    const res = {
      status: jest.fn().mockReturnThis(),
      send: jest.fn(),
      json: jest.fn()
    };

    // Call the function
    await attribution(req, res);

    // Verify the response
    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.send).toHaveBeenCalledWith('Attribution Service is running');
  });

  // Test the attribution endpoint with empty sessions
  test('POST /attribution with empty sessions should return attributed object with empty sessions array', async () => {
    const payload = {
      userId: {
        value: 'user123'
      },
      timestamp: 1625097600000,
      order: {
        id: 'order123',
        timestamp: 1625097600000,
        total: 100.0,
        shipping: 5.0
      },
      sessions: []
    };

    // Create a mock request object
    const req = {
      method: 'POST',
      path: '/attribution',
      headers: {
        'content-type': 'application/json'
      },
      query: {},
      body: payload
    };

    // Create a mock response object
    const res = {
      status: jest.fn().mockReturnThis(),
      send: jest.fn(),
      json: jest.fn()
    };

    // Call the function
    await attribution(req, res);

    // Verify the response
    expect(res.json).toHaveBeenCalledWith({
      userId: {
        value: 'user123'
      },
      timestamp: 1625097600000,
      order: {
        id: 'order123',
        timestamp: 1625097600000,
        total: 100.0,
        shipping: 5.0
      },
      sessions: []
    });
  });

  // Test the attribution endpoint with sessions
  test('POST /attribution with sessions should return attributed sessions with weights', async () => {
    // Mock Math.random to return predictable values
    const originalRandom = Math.random;
    Math.random = jest.fn()
      .mockReturnValueOnce(0.5) // (0.5 * 91) + 10 = 55.5 -> 55
      .mockReturnValueOnce(0.8); // (0.8 * 91) + 10 = 82.8 -> 82

    const payload = {
      userId: {
        value: 'user123'
      },
      timestamp: 1625097600000,
      order: {
        id: 'order123',
        timestamp: 1625097600000,
        total: 100.0,
        shipping: 5.0
      },
      sessions: [
        {
          id: 'session123',
          timestamp: 1625097500000,
          userId: {
            value: 'user123'
          },
          windowFrom: 1625097400000,
          windowTo: 1625097600000,
          pageViewCount: 5,
          durationMillis: 200000,
          marketingChannel: 'ORGANIC',
          landingPage: '/home',
          lastEvent: {
            order: null
          }
        },
        {
          id: 'session456',
          timestamp: 1625097300000,
          userId: {
            value: 'user123'
          },
          windowFrom: 1625097200000,
          windowTo: 1625097400000,
          pageViewCount: 3,
          durationMillis: 150000,
          marketingChannel: 'PAID',
          landingPage: '/products',
          lastEvent: {
            order: null
          }
        }
      ]
    };

    // Create a mock request object
    const req = {
      method: 'POST',
      path: '/attribution',
      headers: {
        'content-type': 'application/json'
      },
      query: {},
      body: payload
    };

    // Create a mock response object
    const res = {
      status: jest.fn().mockReturnThis(),
      send: jest.fn(),
      json: jest.fn()
    };

    // Call the function
    await attribution(req, res);

    // Restore Math.random
    Math.random = originalRandom;

    // Verify the response
    expect(res.json).toHaveBeenCalledWith({
      userId: {
        value: 'user123'
      },
      timestamp: 1625097600000,
      order: {
        id: 'order123',
        timestamp: 1625097600000,
        total: 100.0,
        shipping: 5.0
      },
      sessions: [
        {
          weight: 55,
          session: {
            id: 'session123',
            timestamp: 1625097500000,
            userId: {
              value: 'user123'
            },
            windowFrom: 1625097400000,
            windowTo: 1625097600000,
            pageViewCount: 5,
            durationMillis: 200000,
            marketingChannel: 'ORGANIC',
            landingPage: '/home',
            lastEvent: {
              order: null
            }
          }
        },
        {
          weight: 82,
          session: {
            id: 'session456',
            timestamp: 1625097300000,
            userId: {
              value: 'user123'
            },
            windowFrom: 1625097200000,
            windowTo: 1625097400000,
            pageViewCount: 3,
            durationMillis: 150000,
            marketingChannel: 'PAID',
            landingPage: '/products',
            lastEvent: {
              order: null
            }
          }
        }
      ]
    });
  });

  // Test 404 for unsupported paths
  test('POST to unsupported path should return 404', async () => {
    // Create a mock request object
    const req = {
      method: 'POST',
      path: '/unsupported-path',
      headers: {
        'content-type': 'application/json'
      },
      query: {},
      body: {}
    };

    // Create a mock response object
    const res = {
      status: jest.fn().mockReturnThis(),
      send: jest.fn(),
      json: jest.fn()
    };

    // Call the function
    await attribution(req, res);

    // Verify the response
    expect(res.status).toHaveBeenCalledWith(404);
    expect(res.send).toHaveBeenCalledWith('Not Found');
  });
});
