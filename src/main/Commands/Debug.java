package main.Commands;

import main.Commands.obj.Command;
import main.Commands.obj.CommandArgument;
import main.Commands.obj.RegisterCommand;
import sx.blah.discord.util.EmbedBuilder;

@RegisterCommand(name = "Debug")
public class Debug extends Command {

	//	@Override
	//	public Class<? extends OptionsBase> getOptions() {
	//		return OptionsBase.class;
	//	}

	@SuppressWarnings("unused")
	public void invoke(CommandArgument<OptionsDefault> args) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.appendField("Arguments", "[" + String.join(", ", args.parser.getResidue()) + "]", false);
		args.message.getChannel().sendMessage(embed.build());
	}
	//
	//	public static class Options extends OptionsBase {
	//
	//	}
}
