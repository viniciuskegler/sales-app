FROM node:20-alpine AS builder
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

ARG BUILD_TIME=local
RUN echo "{\"buildTime\":\"$BUILD_TIME\"}" > public/version.json

RUN npm run build

FROM node:20-alpine
WORKDIR /app

COPY package*.json ./
RUN npm ci --omit=dev

COPY --from=builder /app/dist ./dist

EXPOSE 4000
ARG CACHEBUST=none
CMD ["node", "dist/sales-app/server/server.mjs"]
