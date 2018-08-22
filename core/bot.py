import discord
from core import config
from core import events


class MyClient(discord.Client):
    async def on_ready(self):
        await events.on_ready(self, client)

    async def on_message(self, message):
        await events.on_message(self, client, message)


print(_("console.client.login"))
client = MyClient()
client.run(config.token)
