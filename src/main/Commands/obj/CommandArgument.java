package main.Commands.obj;

import sx.blah.discord.handle.obj.IMessage;

public class CommandArgument {
	public IMessage      message;
	public ParamIterator paramIterator;

	public CommandArgument(IMessage message, ParamIterator paramIterator) {
		this.message = message;
		this.paramIterator = paramIterator;
	}
}
