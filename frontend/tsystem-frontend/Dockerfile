# frontend/tsystem-frontend/Dockerfile
FROM node:20-alpine AS development

WORKDIR /app

# Копируем только package файлы для кэширования
# copy only packager files for cache
COPY package*.json ./
RUN npm ci --only=production && npm install

# copy rest of the code
COPY . .

# run dev server
EXPOSE 5173
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]