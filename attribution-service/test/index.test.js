const { attributeSessions } = require('../index');

describe('attributeSessions', () => {
  test('should return object with empty sessions array when input sessions is empty', () => {
    // Arrange
    const input = {
      userId: 'user123',
      timestamp: 1634567890,
      order: { id: 'order123', amount: 100 },
      sessions: []
    };

    // Act
    const result = attributeSessions(input);

    // Assert
    expect(result).toEqual({
      userId: 'user123',
      timestamp: 1634567890,
      order: { id: 'order123', amount: 100 },
      sessions: []
    });
  });

  test('should return object with empty sessions array when input sessions is null', () => {
    // Arrange
    const input = {
      userId: 'user123',
      timestamp: 1634567890,
      order: { id: 'order123', amount: 100 },
      sessions: null
    };

    // Act
    const result = attributeSessions(input);

    // Assert
    expect(result).toEqual({
      userId: 'user123',
      timestamp: 1634567890,
      order: { id: 'order123', amount: 100 },
      sessions: []
    });
  });

  test('should return object with attributed sessions when input sessions is not empty', () => {
    // Arrange
    const input = {
      userId: 'user123',
      timestamp: 1634567890,
      order: { id: 'order123', amount: 100 },
      sessions: [
        { id: 'session1', startTime: 1634567800 },
        { id: 'session2', startTime: 1634567850 }
      ]
    };

    // Mock Math.random to return predictable values
    const originalRandom = Math.random;
    Math.random = jest.fn()
      .mockReturnValueOnce(0.5) // (0.5 * 91) + 10 = 55.5 -> 55
      .mockReturnValueOnce(0.8); // (0.8 * 91) + 10 = 82.8 -> 82

    // Act
    const result = attributeSessions(input);

    // Restore Math.random
    Math.random = originalRandom;

    // Assert
    expect(result).toEqual({
      userId: 'user123',
      timestamp: 1634567890,
      order: { id: 'order123', amount: 100 },
      sessions: [
        {
          weight: 55,
          session: { id: 'session1', startTime: 1634567800 }
        },
        {
          weight: 82,
          session: { id: 'session2', startTime: 1634567850 }
        }
      ]
    });
  });

  test('should generate weights between 10 and 100 for each session', () => {
    // Arrange
    const input = {
      userId: 'user123',
      timestamp: 1634567890,
      order: { id: 'order123', amount: 100 },
      sessions: [
        { id: 'session1', startTime: 1634567800 }
      ]
    };

    // Act
    const result = attributeSessions(input);

    // Assert
    expect(result.sessions[0]).toHaveProperty('weight');
    expect(result.sessions[0].weight).toBeGreaterThanOrEqual(10);
    expect(result.sessions[0].weight).toBeLessThanOrEqual(100);
  });

  test('should correctly attribute sessions from the "Attribution Service - With Sessions" request', () => {
    // Arrange
    const input = {
      "userId": {
        "value": "user123"
      },
      "timestamp": 1625097600000,
      "order": {
        "id": "order123",
        "timestamp": 1625097600000,
        "total": 100.0,
        "shipping": 5.0
      },
      "sessions": [
        {
          "id": "session123",
          "timestamp": 1625097500000,
          "userId": {
            "value": "user123"
          },
          "windowFrom": 1625097400000,
          "windowTo": 1625097600000,
          "pageViewCount": 5,
          "durationMillis": 200000,
          "marketingChannel": "ORGANIC",
          "landingPage": "/home",
          "lastEvent": {
            "order": null
          }
        },
        {
          "id": "session456",
          "timestamp": 1625097300000,
          "userId": {
            "value": "user123"
          },
          "windowFrom": 1625097200000,
          "windowTo": 1625097400000,
          "pageViewCount": 3,
          "durationMillis": 150000,
          "marketingChannel": "PAID",
          "landingPage": "/products",
          "lastEvent": {
            "order": null
          }
        }
      ]
    };

    // Mock Math.random to return predictable values
    const originalRandom = Math.random;
    Math.random = jest.fn()
      .mockReturnValueOnce(0.5) // (0.5 * 91) + 10 = 55.5 -> 55
      .mockReturnValueOnce(0.8); // (0.8 * 91) + 10 = 82.8 -> 82

    // Act
    const result = attributeSessions(input);

    // Restore Math.random
    Math.random = originalRandom;

    // Assert
    expect(result).toEqual({
      "userId": {
        "value": "user123"
      },
      "timestamp": 1625097600000,
      "order": {
        "id": "order123",
        "timestamp": 1625097600000,
        "total": 100.0,
        "shipping": 5.0
      },
      "sessions": [
        {
          weight: 55,
          session: {
            "id": "session123",
            "timestamp": 1625097500000,
            "userId": {
              "value": "user123"
            },
            "windowFrom": 1625097400000,
            "windowTo": 1625097600000,
            "pageViewCount": 5,
            "durationMillis": 200000,
            "marketingChannel": "ORGANIC",
            "landingPage": "/home",
            "lastEvent": {
              "order": null
            }
          }
        },
        {
          weight: 82,
          session: {
            "id": "session456",
            "timestamp": 1625097300000,
            "userId": {
              "value": "user123"
            },
            "windowFrom": 1625097200000,
            "windowTo": 1625097400000,
            "pageViewCount": 3,
            "durationMillis": 150000,
            "marketingChannel": "PAID",
            "landingPage": "/products",
            "lastEvent": {
              "order": null
            }
          }
        }
      ]
    });
  });
});
