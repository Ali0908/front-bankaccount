# Frontend Dockerfile (Angular 20)
# Stage 1: build
FROM node:20-alpine AS build
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm ci

# Copy source
COPY . .

# Allow overriding the API URL at build time (passed via --build-arg or docker-compose args)
ARG API_URL
RUN : "${API_URL:?API_URL build-arg is required}" \ 
	&& sed -i "s#https://api.bankaccount.com#${API_URL}#g" src/environments/environment.ts

# Build Angular app
RUN npm run build

# Stage 2: nginx runtime
FROM nginx:1.27-alpine AS runtime
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Angular output (new builder outputs to /browser)
COPY --from=build /app/dist/front-bankaccount/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
