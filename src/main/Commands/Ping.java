package main.Commands;

import main.Commands.obj.Command;
import main.Commands.obj.CommandArgument;
import main.Commands.obj.Parameter;
import main.Commands.obj.RegisterCommand;
import main.OwO;

import java.util.function.Consumer;

@RegisterCommand
public class Ping extends Command {

	public Ping() {
		this.name="Ping";
		OwO.logger.debug("Hallo there");
	}
	@Override
	public void invoke(CommandArgument arg) {
		arg.message.getChannel().sendMessage("Pong!");
	}

	@Override
	public Command getInstance() {
		return new Ping();
	}

	@Override
	public void collectParameters(Consumer<Parameter> visitor) {
		visitor.accept(Parameter.TEXT);
	}
}
