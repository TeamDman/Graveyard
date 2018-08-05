package main.impl.commands;

import com.google.devtools.common.options.Option;
import main.core.OwO;
import main.core.command.*;
import sx.blah.discord.handle.obj.Permissions;

@RegisterCommand
public class Shutdown extends Command implements IInvocable<Shutdown.Options> {
	public Shutdown() {
		super(new Builder("Shutdown")
				.withCommand("die")
				.withPermission(Permissions.ADMINISTRATOR));
	}

	public void invoke(CommandArguments<Options> args) {
		args.message.getChannel().sendMessage(args.options.restart ? "Restarting" : "Shutting down");
		OwO.exit(args.options.restart ? OwO.ExitLevel.RESTART : OwO.ExitLevel.SUCCESS);
	}

	@CommandOptions("")
	public static class Options extends Command.OptionsDefault {
		@Option(
				name = "restart",
				abbrev = 'r',
				help = "Whether or not to restart the bot after shutting down",
				defaultValue = "false"
		)
		public boolean restart;
	}
}
