# Attribution Service

A Node.js implementation of the AttributionService interface that behaves like DummyAttributionService. This service is designed to be deployed as a Google Cloud Function.

## Overview

This service provides an HTTP API endpoint that accepts OrderWithSessions objects and returns OrderWithAttributedSessions objects. It assigns random weights to sessions for attribution purposes.

## API Endpoints

- `POST /`: Accepts an OrderWithSessions object and returns an OrderWithAttributedSessions object
- `POST /attribution`: Alternative endpoint that does the same as above
- `GET /`: Health check endpoint

## Request/Response Format

### Request (POST /attribution)

```json
{
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
    }
  ]
}
```

### Response

```json
{
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
      "weight": 75,
      "session": {
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
    }
  ]
}
```

## Deployment to Google Cloud Functions

1. Install dependencies:
   ```
   cd attribution-service
   npm install
   ```

2. Deploy to Google Cloud Functions:
   ```
   gcloud functions deploy attribution \
     --runtime nodejs18 \
     --trigger-http \
     --entry-point attribution \
     --region [REGION] \
     --allow-unauthenticated
   ```

## Local Development

1. Install dependencies:
   ```
   npm install
   ```

2. Start the functions framework:
   ```
   npm start
   ```

3. The function will be available at http://localhost:8080
