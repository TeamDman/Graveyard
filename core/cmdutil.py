from core import config
import discord
import re


class ArgumentException(Exception):
    pass


class ExecutionException(Exception):
    pass


def map_arg(param, client, guild, identifier):
    switch = {
        "${NONE}": lambda c, g, x: "",
        "${STRING}": lambda c, g, x: x,
        "${NUMBER}": get_number,
        "${USER}": get_user,
        "${MEMBER}": get_member,
        "${ROLE}": get_role,
        "${CHANNEL}": get_channel
    }
    return switch.get(param, lambda c, g, x: x)(client, guild, identifier)


def build_args(client, command, message, args):
    if len(args) == 0:
        args = ["${NONE}"]
    if args[0] not in command.params:
        raise ArgumentException(_("exception.arguments.route_missing"))
    route = args[0]
    args = args[1:]
    params = command.params[route]
    if len(args) != len(params):
        raise ArgumentException(_("exception.arguments.mismatch").format(len(args), len(params)))
    new_args = [map_arg(param, client, message.server, args[i]) for i, param in enumerate(params)]
    if None in new_args:
        i = new_args.index(None)
        raise ArgumentException(_("exception.arguments.parse_failed").format(args[i], params[i]))
    return [route] + new_args


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
