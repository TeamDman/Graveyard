FROM node:latest
ENV PATH="/app/bin:${PATH}"
ARG PORT
ENV PORT=${PORT:-1997}

RUN mkdir /app
WORKDIR /app/

COPY package.json /app/
COPY package-lock.json /app/
RUN npm i

COPY app.js /app/
COPY .profile.d /app/.profile.d
COPY bin/ /app/bin/

RUN chmod +x /app/bin/*
CMD bash heroku-exec.sh && node /app/app.js
#CMD ["node","/app/app.js"]
#CMD ["bash"]
