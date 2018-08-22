from core import config
import discord
import re


class ArgumentException(Exception):
    pass


class ExecutionException(Exception):
    pass


def map_arg(params, param_name, args, index, client, guild):
    switch = {
        config.commands["parameters"]["string"]: lambda c, g, x: x,
        config.commands["parameters"]["number"]: get_number,
        config.commands["parameters"]["user"]: get_user,
        config.commands["parameters"]["member"]: get_member,
        config.commands["parameters"]["role"]: get_role,
        config.commands["parameters"]["channel"]: get_channel
    }
    if param_name == config.commands["parameters"]["remaining"]:
        convert = switch.get(params[param_name])
        return [convert(client, guild, arg) for arg in args[index:]]
    return switch.get(params[param_name])(client, guild, args[index])


def build_args(client, command, message, args):
    if len(args) == 0:
        args = [config.commands["routes"]["none"]]
    if config.commands["routes"]["routeless"] in command.params:
        route = config.commands["routes"]["routeless"]
        new_args = []
    else:
        route = args[0]
        args = args[1:]
        new_args = [route]
    if route not in command.params:
        raise ArgumentException(_("exception.arguments.route_missing"))
    params = command.params[route]
    if len(args) != len(params) and config.commands["parameters"]["remaining"] not in params:
        raise ArgumentException(_("exception.arguments.mismatch").format(len(args), len(params)))
    for i, param_name in enumerate(params.keys()):
        new_args += map_arg(params, param_name, args, i, client, message.server)
    if None in new_args:
        i = new_args.index(None)
        raise ArgumentException(_("exception.arguments.parse_failed").format(args[i], params[i]))
    return new_args


def trim_mention(mention):
    matches = re.findall(r"<[#&]!?(\d{18})>", mention)
    if len(matches) == 0:
        return mention
    return matches[0]


def get_number(client, guild, identifier):
    return int(identifier)
    """Gets a number from the given string"""
    pass


def get_channel(client, guild, identifier):
    """Gets a channel from the given guild"""
    identifier = trim_mention(identifier).lower()
    for channel in guild.channels:
        if channel.id == identifier or channel.name.lower() == identifier:
            return channel
    return None


def get_member(client, guild, identifier):
    """Gets a member from the given guild"""
    identifier = trim_mention(identifier).lower()
    return next((member for member in guild.members if
                 member.id == identifier or member.discriminator.lower() == identifier or member.name.lower() == identifier),
                None)


def get_user(client, guild, identifier):
    """Gets a user, searching from all guilds"""
    identifier = trim_mention(identifier).lower()
    for server in client.servers:
        for member in server.members:
            if member.id == identifier or member.discriminator.lower() == identifier or member.name.lower() == identifier:
                return member
    return None


def get_role(client, guild, identifier):
    """Gets a role from the given guild"""
    pass
