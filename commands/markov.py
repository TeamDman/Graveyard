import discord
import commands
import markovify
from core import cmdutil
import os


class Markov:
    text_models = {}

    @staticmethod
    def write_models():
        for channel, models in Markov.text_models.items():
            os.makedirs("../resources/markov_models/{}".format(channel))
            for user, model in models.items():
                with open("../resources/markov_models/{}/{}.json".format(channel, user), "w") as f:
                    f.write(model.to_json())

    @staticmethod
    @commands.register_command
    async def __call__(client, message, args):
        if args[0] == "import":
            with open("../resources/corpus.txt", encoding="UTF-8") as f:
                corpus = f.read()
            await client.send_message(message.channel, _("response.info.markov.load_start").format(len(corpus)))
            Markov.text_models["1"] = markovify.NewlineText(Markov.corpus)
            await client.send_message(message.channel, _("response.info.markov.load_end"))
        if args[0] == "say":
            if args[2].id not in Markov.text_models:
                raise cmdutil.ExecutionException(_("exception.markov.model_channel_missing").format(args[2]))
            channel_models = Markov.text_models[args[2].id]
            if args[1].id not in channel_models:
                raise cmdutil.ExecutionException(_("exception.markov.model_member_missing").format(args[1], args[2]))
            user_model = channel_models[args[1].id]
            em = discord.Embed()
            em.description = "Markov chains of <@{}>'s messages\n\n".format(args[1].id)
            for i in range(5):
                em.description += user_model.make_short_sentence(140) + "\n\n"
            await client.send_message(message.channel, embed=em)
        if args[0] == "scry":
            await client.send_typing(message.channel)
            corpuses = {}
            total = 0
            msg_iter = client.logs_from(args[1], limit=args[2])
            async for msg in msg_iter:
                if msg.author.id not in corpuses:
                    corpuses[msg.author.id] = msg.content
                else:
                    corpuses[msg.author.id] += msg.content + "\n"
                total += len(msg.content)
            Markov.text_models[args[1].id] = {}
            await client.send_message(message.channel, _("response.info.markov.load_start").format(total))
            for author, corpus in corpuses.items():
                if len(corpus) > 0:
                    Markov.text_models[args[1].id][author] = markovify.NewlineText(corpus)
            await client.send_message(message.channel, _("response.info.markov.load_end"))
        #             todo: editing updating parsing status
        if args[0] == "save":
            Markov.write_models()
            await client.send_message(message.channel, _("response.info.markov.file.write"))
        if args[0] == "load":
            await client.send_message(message.channel, _("response.info.markov.file.read"))
            for root, channels, files in os.walk("../resources/markov_models"):
                for f in os.scandir("../resources/markov_models"):
                    if f.is_dir():
                        for json in os.scandir(f):
                            if json.is_file():
                                print(json)

