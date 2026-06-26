# ===== 构建阶段 =====
FROM node:16-alpine AS build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci --only=production --no-audit --no-fund
COPY frontend/ ./
RUN npm run build

# ===== 运行阶段 =====
FROM nginx:1.25-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY sites/xingxing /usr/share/nginx/xingxing
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
