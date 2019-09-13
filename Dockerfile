FROM node:latest
RUN mkdir /app
WORKDIR /app/
COPY package.json /app/
COPY package-lock.json /app/

RUN npm i
COPY . /app/

CMD ["node","/app/app.js"]