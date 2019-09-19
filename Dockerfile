FROM node:latest
ENV APPSTATE
ENV PATH="/app/bin:${PATH}"

RUN mkdir /app
WORKDIR /app/

COPY package.json /app/
COPY package-lock.json /app/
RUN npm i

COPY app.js /app/
COPY bin/ /app/bin/

RUN chmod +x /app/bin/*
CMD ["node","/app/app.js"]
#CMD ["bash"]