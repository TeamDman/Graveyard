package main.Commands;

import com.google.common.collect.Lists;
import main.Commands.obj.Command;
import main.Commands.obj.CommandArgument;
import main.Commands.obj.RegisterCommand;
import main.Listeners.PaginatorListener;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.EmbedBuilder;

import java.util.stream.Collectors;

@RegisterCommand(name = "Help", cmds = {"help", "?", "info"})
public class Help extends Command {
	@SuppressWarnings("unused")
	public void invoke(CommandArgument<OptionsDefault> args) {
		//		IMessage msg = args.message.getChannel().sendMessage()
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
						.withTitle("Role Information ("+args.message.getGuild().getRoles().size() + " roles)")
						.withAuthorIcon(args.message.getAuthor().getAvatarURL())
						.withAuthorName(args.message.getAuthor().getName())
						.appendDesc(args.message.getGuild().getRoles().stream().map(r -> "<@&"+r.getStringID()+">").collect(Collectors.joining("\n")))
						.build()
		));
	}
}
