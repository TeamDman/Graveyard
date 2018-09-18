import commands
import discord


class RoleInfo:
    @staticmethod
    @commands.register_command
    async def __call__(client, message, args):
        em = discord.Embed()
        em.add_field(name="id", value=args[0].id)
        await client.send_message(message.channel, embed=em)
