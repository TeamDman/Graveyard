FROM python:3.6

WORKDIR /app

RUN pip3 install discord.py==0.16.12 ruamel.yaml markovify
COPY . /app
CMD ["python3","start.py"]

#  manually run it with sh
# cd /app/
# python start.py