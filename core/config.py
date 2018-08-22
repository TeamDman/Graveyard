from ruamel.yaml import YAML
import re
import os

os.chdir("config")

token = YAML().load(open("token.yaml"))["token"]

core = YAML().load(open("config.yaml"))
prefix = re.compile(core["prefix"])

commands = YAML().load(open("commands.yaml"))
