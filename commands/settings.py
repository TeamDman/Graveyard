import commands


class Settings:
    @staticmethod
    @commands.register_command
    async def __call__(client, message, args):
        print("zoop")
