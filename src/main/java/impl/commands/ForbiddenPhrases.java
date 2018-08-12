package impl.commands;

import com.google.common.collect.Lists;
import com.google.devtools.common.options.Option;
import core.command.*;
import core.listener.PaginatorListener;
import impl.module.ForbiddenPhraseReactor;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.stream.Collectors;

@RegisterCommand
public class ForbiddenPhrases extends Command implements IInvocable<ForbiddenPhrases.Options> {
	public ForbiddenPhrases() {
		super(new Builder("ForbiddenPhrases").withCommand("fp").withSchema("-a $"));
	}

	@Override
	public void invoke(CommandArguments<Options> args) throws Throwable {
		if (args.options.populate) {
			Lists.newArrayList(
					"<@\\!?(\\d{18})>.*?<@\\!?(?!\\1)(\\d{18})>.*?<@\\!?(?!(?:\\1|\\2))(\\d{18})>.*?<@\\!?(?!(?:\\1|\\2))(\\d{18})>.*?<@\\!?(?!(?:\\1|\\2|\\3))(\\d{18})>.*?<@\\!?(?!(?:\\1|\\2|\\3|\\4))(\\d{18})>"
			).forEach(ForbiddenPhraseReactor::addPhrase);
			RequestBuffer.request(() -> args.message.getChannel().sendMessage("Populated the list with default phrases."));
			args.options.listphrases = false;
		}
		if (!args.options.addphrase.isEmpty()) {
			ForbiddenPhraseReactor.addPhrase(args.options.addphrase);
			RequestBuffer.request(() -> args.message.getChannel().sendMessage("Added phrase to the list."));
			args.options.listphrases = false;
		}
		if (!args.options.removephrase.isEmpty()) {
			if (ForbiddenPhraseReactor.removePhrase(args.options.removephrase))
				RequestBuffer.request(() -> args.message.getChannel().sendMessage("Removed phrase from the list."));
			else
				RequestBuffer.request(() -> args.message.getChannel().sendMessage("Phrase was not found in the list."));
			args.options.listphrases = false;
		}
		if (args.options.listphrases)
			if (ForbiddenPhraseReactor.getPhrases().size() == 0)
				RequestBuffer.request(() -> args.message.getChannel().sendMessage("There are no forbidden phrases at this time."));
			else
				new PaginatorListener(args.message.getChannel(), args.message.getAuthor(), Lists.partition(ForbiddenPhraseReactor.getPhrases(), 10).stream()
						.map(list -> {
							EmbedBuilder embed = new EmbedBuilder().withTitle("Phrases");
							list.forEach(c -> embed.appendDesc(c + "\n"));
							return embed;
						}).collect(Collectors.toList()));
	}

	@RegisterOptions
	public static class Options extends OptionsDefault {
		@Option(
				name = "addphrase",
				abbrev = 'a',
				help = "The phrase to add to the list",
				defaultValue = ""
		)
		public String  addphrase;
		@Option(
				name = "listphrases",
				abbrev = 'l',
				help = "Whether to list all phrases or not",
				defaultValue = "true"
		)
		public boolean listphrases;
		@Option(
				name = "populate",
				abbrev = 'p',
				help = "Populate the database with default phrases",
				defaultValue = "false"
		)
		public boolean populate;
		@Option(
				name = "removephrase",
				abbrev = 'r',
				help = "The phrase to remove from the list",
				defaultValue = ""
		)
		public String  removephrase;
	}
}
