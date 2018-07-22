package main.Commands;

import com.google.devtools.common.options.Option;
import main.Commands.obj.Command;
import main.Commands.obj.CommandArgument;
import main.Commands.obj.RegisterCommand;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Timer;
import java.util.TimerTask;

@RegisterCommand(name = "Ping", cmds={"ping"})
public class Ping extends Command {

	@SuppressWarnings("unused")
	public void invoke(CommandArgument<Options> arg) {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				IMessage msg = arg.message.getChannel().sendMessage(arg.options.mention ? arg.message.getAuthor().mention() : "Pong!");
				if (arg.options.time)
					msg.edit("Ping! Latency is " + (msg.getTimestamp().toEpochMilli() - arg.message.getTimestamp().toEpochMilli()) + "ms");
			}
		}, arg.options.delay);
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

		@Option(
				name = "mention",
				abbrev = 'm',
				help = "Makes the bot mention you.",
				defaultValue = "false"
		)
		public boolean mention;


		@Option(
				name = "delay",
				abbrev = 'd',
				help = "Delay the ping message by a set amount.",
				defaultValue = "0"
		)
		public int delay;
	}
}
