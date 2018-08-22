from core import cmdutil
from core import config
import discord
import commands


async def on_ready(clazz, client):
    print('-----')
    print('Logged in as')
    print(client.user.name)
    print(client.user.id)
    print('-----')


async def on_message(clazz, client, message):
    if not message.author.id == "159018622600216577":
        return
    if not config.prefix.match(message.content):
        return

    args = message.content[len(config.core["prefix"]):].split()

    for name, command in commands.commandDict.items():
        if any(alias.match(args[0]) for alias in command.aliases):
            try:
                del args[0]
                args = cmdutil.build_args(client, command, message, args)
                await command.call(client, message, args or [])
            except cmdutil.ArgumentException as e:
                em = discord.Embed(colour=0xFFA500)
                em.description = _("response.error.arguments").format(e.args[0])
                await client.send_message(message.channel, embed=em)
            except cmdutil.ExecutionException as e:
                em = discord.Embed(colour=0xFFA500)
                em.description = _("response.error.execution").format(e.args[0])
                await client.send_message(message.channel, embed=em)
            print(_("console.command.execute").format(name))
            break
