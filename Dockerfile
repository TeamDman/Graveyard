FROM node:latest
ENV PATH="/app/bin:${PATH}"

RUN mkdir /app
WORKDIR /app/
COPY package.json /app/
COPY package-lock.json /app/

RUN npm i
COPY . /app/
RUN chmod +x /app/bin/*
CMD ["node","/app/app.js"]
#CMD ["bash"]