import commands


class Eval:
    @staticmethod
    @commands.register_command
    async def __call__(client, message, args):
        result = eval(str.join(" ", args))
        await client.send_message(message.channel, result)
