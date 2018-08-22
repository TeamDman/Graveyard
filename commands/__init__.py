import inspect
import pkgutil
import time
from core import config
import re
import gettext


class Command:
    def __init__(self, name, call, aliases, params, desc):
        self.name = name
        self.call = call
        self.aliases = [re.compile(alias) for alias in aliases]
        self.params = params
        self.desc = desc


def register_command(f):
    module = inspect.getmodule(f)
    module_config = config.commands["commands"][module.commands.name]
    commandDict[module.commands.name] = Command(
        module.commands.name,
        f,
        (module_config["aliases"] if "aliases" in module_config else []) + [module.commands.name],
        module_config["params"] if "params" in module_config else config.commands["defaults"]["params"],
        module_config["desc"]
    )
    print(_("console.command.register").format(module.commands.name))
    return f


start = time.time()
commandDict = {}

for importer, name, ispkg in pkgutil.iter_modules(__path__):
    __import__("{}.{}".format(__package__, name), fromlist=__package__)
print(_("console.command.register.finish").format(len(commandDict), time.time() - start))
