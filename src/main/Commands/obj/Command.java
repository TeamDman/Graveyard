package main.Commands.obj;

import java.util.function.Consumer;

public abstract class Command {

	public String name;

	public abstract void invoke(CommandArgument argument);

	public abstract Command getInstance();

	public abstract void collectParameters(Consumer<Parameter> visitor);
}
