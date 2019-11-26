FROM python:3.6

WORKDIR /app

RUN pip3 install discord.py==0.16.12 ruamel.yaml markovify==0.7.2
COPY . /app
CMD ["python3","start.py"]

# docker run --rm -ti imagename sh -c "cd /app/ && python start.py"