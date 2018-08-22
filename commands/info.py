import commands


class Info:
    @staticmethod
    @commands.register_command
    async def __call__(client, message, args):
        await client.send_message(message.channel, "info")
