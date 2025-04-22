// Attribution Service implementation for Google Cloud Functions
const functions = require('@google-cloud/functions-framework');

// Main attribution function that mimics DummyAttributionService behavior
function attributeSessions(orderWithSessions) {
  // Extract data from the input
  const { userId, timestamp, order, sessions } = orderWithSessions;

  // If sessions list is empty, return with empty attributed sessions
  if (!sessions || sessions.length === 0) {
    return {
      userId,
      timestamp,
      order,
      sessions: []
    };
  }

  // Create attributed sessions with random weights between 10 and 100
  const attributedSessions = sessions.map(session => {
    const weight = Math.floor(Math.random() * 91) + 10; // Random number between 10 and 100
    return {
      weight,
      session
    };
  });

  // Return the OrderWithAttributedSessions object
  return {
    userId,
    timestamp,
    order,
    sessions: attributedSessions
  };
}

// Register HTTP function for attribution
functions.http('attribution', (req, res) => {
  // Check if it's a health check request
  if (req.method === 'GET' && req.path === '/') {
    res.status(200).send('Attribution Service is running');
    return;
  }

  // Handle attribution request
  if (req.method === 'POST' && (req.path === '/attribution' || req.path === '/')) {
    try {
      const orderWithSessions = req.body;
      const result = attributeSessions(orderWithSessions);
      res.json(result);
    } catch (error) {
      console.error('Error processing attribution request:', error);
      res.status(500).json({ error: 'Internal server error' });
    }
    return;
  }

  // Handle unsupported methods/paths
  res.status(404).send('Not Found');
});

// For testing/debugging purposes
module.exports = { attributeSessions };
