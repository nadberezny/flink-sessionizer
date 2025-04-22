FROM node:18-slim

WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm ci --only=production

# Copy application code
COPY index.js ./

# Expose the port that the functions framework will run on
EXPOSE 8080

# Set environment variables
ENV NODE_ENV=production
ENV PORT=8080

# Start the service
CMD ["npm", "start"]
