package main.Commands.obj;

import com.google.devtools.common.options.OptionsBase;
import sx.blah.discord.handle.obj.IMessage;

public class CommandArgument<T extends OptionsBase> {
	public IMessage message;
	public T        options;

	public CommandArgument(IMessage message, T options) {
		this.message = message;
		this.options = options;
	}
}
