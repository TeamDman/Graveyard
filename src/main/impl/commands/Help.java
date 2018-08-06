package main.impl.commands;

import com.google.common.collect.Lists;
import main.core.command.Command;
import main.core.command.CommandArguments;
import main.core.command.IInvocable;
import main.core.command.RegisterCommand;
import main.core.listener.PaginatorListener;
import sx.blah.discord.util.EmbedBuilder;

import java.util.stream.Collectors;

@RegisterCommand
public class Help extends Command implements IInvocable<Command.OptionsDefault> {
	public Help() {
		super(new Builder("Help").withCommand("?").withCommand("info"));
	}

	@SuppressWarnings("unused")
	public void invoke(CommandArguments<OptionsDefault> args) {
		PaginatorListener paginator = new PaginatorListener(args.message, Lists.newArrayList(
				new EmbedBuilder()
						.withTitle(args.message.getGuild().getName() + " Information")
						.withAuthorIcon(args.message.getAuthor().getAvatarURL())
						.withAuthorName(args.message.getAuthor().getName())
						.withThumbnail(args.message.getGuild().getIconURL())
						.appendField("Owner", args.message.getGuild().getOwner().mention(), true)
						.appendField("Roles", args.message.getGuild().getRoles().size() + " roles", true)
						.appendField("Channels", args.message.getGuild().getChannels().size() + " channels", true)
						.appendField("Guild Users", args.message.getGuild().getTotalMemberCount() + " users", true)
						.appendField("Inactive Members", args.message.getGuild().getUsersToBePruned(30) + " inactive users (30 days)", true)
						.build(),
				new EmbedBuilder()
						.withTitle("Role Information (" + args.message.getGuild().getRoles().size() + " roles)")
						.withAuthorIcon(args.message.getAuthor().getAvatarURL())
						.withAuthorName(args.message.getAuthor().getName())
						.appendDesc(args.message.getGuild().getRoles().stream().map(r -> "<@&" + r.getStringID() + ">").collect(Collectors.joining("\n")))
						.build()
		));
	}
}
