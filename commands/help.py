import commands


class Help:
    @staticmethod
    @commands.register_command
    async def __call__(client, message, args):
        print("woot")
        await client.send_message(message.channel,"heelp")
