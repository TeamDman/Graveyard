package main.Commands;

import com.google.common.collect.Lists;
import main.Commands.obj.Command;
import main.Commands.obj.CommandArgument;
import main.Commands.obj.RegisterCommand;
import sx.blah.discord.util.EmbedBuilder;

@RegisterCommand()
public class Debug extends Command {
	public Debug() {
		super("Debug", "debug", null, null);
	}

	@SuppressWarnings("unused")
	public void invoke(CommandArgument<OptionsDefault> args) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.appendField("Arguments", "[" + String.join(", ", args.parser.getResidue()) + "]", false);
		args.message.getChannel().sendMessage(embed.build());
	}
}
