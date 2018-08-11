package impl.commands;

import com.google.common.collect.Lists;
import com.google.devtools.common.options.EnumConverter;
import com.google.devtools.common.options.Option;
import core.command.*;
import core.handler.CommandHandler;
import core.listener.PaginatorListener;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.stream.Collectors;

@RegisterCommand
public class Help extends Command implements IInvocable<Help.Options> {
	public Help() {
		super(new Builder("Help").withCommand("?").withCommand("info").withSchema("-i $"));
	}

	@SuppressWarnings("unused")
	public void invoke(CommandArguments<Options> args) {
		switch (args.options.infotype) {
			case BOT:
				RequestBuffer.request(() -> args.message.getChannel().sendMessage(new EmbedBuilder()
						.withTitle("OwO Bot")
						.appendField("Info", "OwO-Bot is a tool written by <@159018622600216577>.", true)
						.appendField("Usage", "Command syntax involves using the prefix followed by flags." +
								"\nCommand schemas can be used to interpret arguments as flags." +
								"\nFor example, `OwO debug first second third fourth` would be interpreted" +
								"\n as `OwO debug -a first -b second -c third -d fourth`", true)
						.appendField("Commands", "For a list of commands, try using `OwO help -c`" +
								"\nEvery command supports the `--help` flag for usage details.", true)
						.build()));
				break;
			case COMMANDS:
				new PaginatorListener(args.message.getChannel(), args.message.getAuthor(), Lists.partition(CommandHandler.commands, 9).stream()
						.map(list -> {
							EmbedBuilder embed = new EmbedBuilder().withTitle("Commands");
							list.forEach(c -> embed.appendField(c.getName(), String.join(", ",c.getCommands()),true));
							return embed;
						}).collect(Collectors.toList()));
				break;
			case SERVER:
				new PaginatorListener(args.message.getChannel(), args.message.getAuthor(), Lists.newArrayList(
						new EmbedBuilder()
								.withTitle(args.message.getGuild().getName() + " Information")
								.withAuthorIcon(args.message.getAuthor().getAvatarURL())
								.withAuthorName(args.message.getAuthor().getName())
								.withThumbnail(args.message.getGuild().getIconURL())
								.appendField("Owner", args.message.getGuild().getOwner().mention(), true)
								.appendField("Roles", args.message.getGuild().getRoles().size() + " roles", true)
								.appendField("Channels", args.message.getGuild().getChannels().size() + " channels", true)
								.appendField("Guild Users", args.message.getGuild().getTotalMemberCount() + " users", true)
								.appendField("Inactive Members", args.message.getGuild().getUsersToBePruned(30) + " inactive users (30 days)", true),
						new EmbedBuilder()
								.withTitle("Role Information (" + args.message.getGuild().getRoles().size() + " roles)")
								.withAuthorIcon(args.message.getAuthor().getAvatarURL())
								.withAuthorName(args.message.getAuthor().getName())
								.appendDesc(args.message.getGuild().getRoles().stream().map(r -> "<@&" + r.getStringID() + ">").collect(Collectors.joining("\n")))
				));
				break;
		}
	}

	public enum InfoType {
		BOT,
		COMMANDS,
		SERVER
	}

	public static class InfoTypeConverter extends EnumConverter<InfoType> {
		public InfoTypeConverter() {
			super(InfoType.class, "InfoType");
		}
	}

	@RegisterOptions
	public static class Options extends OptionsDefault {
		@Option(
				name = "infotype",
				abbrev = 'i',
				help = "The type of information to display",
				defaultValue = "bot",
				converter = InfoTypeConverter.class
		)
		public InfoType infotype;
	}
}
