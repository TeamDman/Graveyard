import discord
import commands


class Eval:
    @staticmethod
    @commands.register_command
    async def __call__(client, message, args):
        try:
            result = str(eval(str.join(" ", args)))
        except Exception as e:
            result = str(e)
        em = discord.Embed()
        em.description = result
        await client.send_message(message.channel, embed=em)
