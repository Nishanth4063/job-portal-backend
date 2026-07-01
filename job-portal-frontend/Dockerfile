# Stage 1: Build the Angular application using Node.js
FROM node:20-alpine AS build
WORKDIR /app

# Copy dependency manifests and install them
COPY package*.json ./
RUN npm ci

# Copy full application code and compile production assets
COPY . .
RUN npm run build -- --configuration=production

# Stage 2: Serve the compiled static assets using a high-performance Nginx server
FROM nginx:alpine
COPY --from=build /app/dist/job-portal-frontend/browser /usr/share/nginx/html

# 🎯 THE PROXY FIX: Override Nginx default configuration with your custom reverse proxy settings
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose standard web traffic port
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]