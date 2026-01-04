FROM node:20-alpine AS build
WORKDIR /app
ARG VITE_API_BASE
ARG VITE_FILE_VIEWER_BASE
ENV VITE_API_BASE=${VITE_API_BASE}
ENV VITE_FILE_VIEWER_BASE=${VITE_FILE_VIEWER_BASE}
RUN corepack enable
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
COPY . .
RUN pnpm run build

FROM nginx:1.27-alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
