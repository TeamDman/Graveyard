package main.impl.commands;

import main.core.command.Command;
import main.core.command.CommandArguments;
import main.core.command.IInvocable;
import main.core.command.RegisterCommand;
import main.core.handler.DatabaseHandler;
import main.impl.handler.IdleRPGHandler;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

@RegisterCommand
public class IdleRPG extends Command implements IInvocable<IdleRPG.Options> {
	public IdleRPG() {
		super(new Builder("IdleRPG"));
	}

	@Override
	public void invoke(CommandArguments<Options> args) throws Throwable {
		IdleRPGHandler.User user = DatabaseHandler.getUser(args.message.getAuthor());
		RequestBuffer.request(() -> args.message.getChannel().sendMessage(new EmbedBuilder()
				.withTitle("IdleRPG")
				.withAuthorIcon(args.message.getAuthor().getAvatarURL())
				.withAuthorName(args.message.getAuthor().getName())
				.appendDesc("Level: " + user.level)
				.appendDesc("\nNext Levelup: " + IdleRPGHandler.deltaLevelup(user.levelup) + " seconds")
				.build()));
	}


	public static class Options extends OptionsDefault {

	}
}
