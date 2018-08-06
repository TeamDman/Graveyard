package impl.commands;

import com.google.devtools.common.options.Option;
import core.command.*;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

import java.util.Timer;
import java.util.TimerTask;

@RegisterCommand
public class Ping extends Command implements IInvocable<Ping.Options> {
	public Ping() {
		super(new Builder("Ping"));
	}

	public void invoke(CommandArguments<Options> arg) {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				RequestBuffer.request(() -> {
					IMessage msg = arg.message.getChannel().sendMessage(arg.options.mention ? arg.message.getAuthor().mention() : "Pong!");
					if (arg.options.time) {
						msg.edit("Ping! Latency is " + (msg.getTimestamp().toEpochMilli() - arg.message.getTimestamp().toEpochMilli()) + "ms");
					}
				});
			}
		}, arg.options.delay);
	}

	@CommandOptions
	public static class Options extends OptionsDefault {
		@Option(
				name = "delay",
				abbrev = 'd',
				help = "Delay the ping message by a set amount.",
				defaultValue = "0"
		)
		public int delay;
		@Option(
				name = "mention",
				abbrev = 'm',
				help = "Makes the bot mention you.",
				defaultValue = "false"
		)
		public boolean mention;
		@Option(
				name = "time",
				abbrev = 't',
				help = "Displays the response time in ms.",
				defaultValue = "false"
		)
		public boolean time;
	}
}
