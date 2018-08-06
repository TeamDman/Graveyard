package main.impl.commands;

import com.google.devtools.common.options.OptionsBase;
import main.core.command.Command;
import main.core.command.CommandArguments;
import main.core.command.IInvocable;
import main.core.command.RegisterCommand;
import main.core.handler.DatabaseHandler;

@RegisterCommand
public class IdleRPG extends Command implements IInvocable<IdleRPG.Options> {
	public IdleRPG() {
		super(new Builder("IdleRPG"));
	}

	@Override
	public void invoke(CommandArguments<Options> args) throws Throwable {
		DatabaseHandler.insert(args.message.getAuthor(),args.message.getGuild());
	}


	public static class Options extends OptionsDefault {

	}
}
