# syntax=docker/dockerfile:1

# ---- Build ----
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# ---- Runtime (nginx) ----
FROM nginx:1.27-alpine AS runtime
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/pa-front/browser /usr/share/nginx/html
EXPOSE 80
