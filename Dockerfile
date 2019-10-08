FROM node:latest
ENV PATH="/app/bin:${PATH}"
ARG PORT
ENV PORT=${PORT:-1997}

RUN mkdir /app
WORKDIR /app/

RUN apt-get update
RUN apt-get install -y openssh-server
ADD heroku-exec.sh /app/.profile.d/heroku-exec.sh

COPY package.json /app/
COPY package-lock.json /app/
RUN npm i

COPY app.js /app/
COPY bin/ /app/bin/

RUN chmod +x /app/bin/*
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
CMD bash /app/.profile.d/heroku-exec.sh && node /app/app.js
#CMD ["node","/app/app.js"]
#CMD ["bash"]
