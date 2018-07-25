package main.Commands;

import com.google.devtools.common.options.Option;
import main.Commands.obj.Command;
import main.Commands.obj.CommandArgument;
import main.Commands.obj.RegisterCommand;
import main.OwO;
import sx.blah.discord.handle.obj.Permissions;

import java.util.EnumSet;

@RegisterCommand()
public class Shutdown extends Command {
	public Shutdown() {
		super("Shutdown", new String[]{"shutdown", "die"}, Options.class, EnumSet.of(Permissions.ADMINISTRATOR));
	}

	public static class Options extends Command.OptionsDefault {
		@Option(
				name = "Restart",
				abbrev = 'r',
				help = "Whether or not to restart the bot after shutting down",
				defaultValue = "false"
		)
		public boolean restart;
	}

	public void invoke(CommandArgument<Options> args) {
		args.message.getChannel().sendMessage(args.options.restart ? "Restarting" : "Shutting down");
		OwO.exit(args.options.restart ? OwO.ExitLevel.RESTART : OwO.ExitLevel.SUCCESS);
	}
}
