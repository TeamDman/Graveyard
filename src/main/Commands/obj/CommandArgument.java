package main.Commands.obj;

import com.google.devtools.common.options.OptionsBase;
import com.google.devtools.common.options.OptionsParser;
import sx.blah.discord.handle.obj.IMessage;

public class CommandArgument<T extends Command.OptionsDefault> {
	public Command command;
	public IMessage message;
	public T        options;
	public OptionsParser parser;

	public CommandArgument(Command command, IMessage message, T options, OptionsParser parser) {
		this.command = command;
		this.message = message;
		this.options = options;
		this.parser = parser;
	}
}
