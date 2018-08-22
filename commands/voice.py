import commands


class Voice:
    @staticmethod
    @commands.register_command
    async def __call__(client, message, args):
        if args[0] == "join":
            await client.join_voice_channel(args[1])
        if args[0] == "leave":
            await client.voice_client_in(message.server).disconnect()

