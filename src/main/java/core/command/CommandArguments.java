package core.command;

import com.google.devtools.common.options.OptionsParser;
import sx.blah.discord.handle.obj.IMessage;

public class CommandArguments<T extends Command.OptionsDefault> {
	public final Command       command;
	public final IMessage      message;
	public final T             options;
	public final OptionsParser parser;

	public CommandArguments(Command command, IMessage message, T options, OptionsParser parser) {
		this.command = command;
		this.message = message;
		this.options = options;
		this.parser = parser;
	}
}
