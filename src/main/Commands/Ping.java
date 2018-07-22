package main.Commands;

import com.google.devtools.common.options.Option;
import main.Commands.obj.Command;
import main.Commands.obj.CommandArgument;
import main.Commands.obj.RegisterCommand;
import sx.blah.discord.handle.obj.IMessage;

@RegisterCommand(name = "Ping")
public class Ping extends Command {

	@SuppressWarnings("unused")
	public void invoke(CommandArgument<Options> arg) {
		if (arg.options.time) {
			IMessage msg = arg.message.getChannel().sendMessage("Pong!");
			msg.edit("Ping! Latency is " + (msg.getTimestamp().toEpochMilli() - arg.message.getTimestamp().toEpochMilli()) + "ms");
		} else
			arg.message.getChannel().sendMessage("Pong!");
	}

	@Override
	public Class<? extends OptionsDefault> getOptions() {
		return Options.class;
	}

	public static class Options extends OptionsDefault {
		@Option(
				name = "time",
				abbrev = 't',
				help = "Displays the response time in ms.",
				defaultValue = "false"
		)
		public boolean time;
	}
}
