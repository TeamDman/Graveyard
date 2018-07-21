package main.Commands;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;
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
	}

	public static class Options extends OptionsDefault {
		@Option(
				name="Time",
				abbrev = 't',
				help = "Displays the response time in ms.",
				defaultValue = "false"
		)
		public boolean time;
	}

	public void invoke(CommandArgument<Options> arg) {
		arg.message.getChannel().sendMessage("Pong!");
	}

	@Override
	public Command getInstance() {
		return new Ping();
	}

	@Override
	public Class<? extends OptionsBase> getOptions() {
		return Options.class;
	}

	@Override
	public void collectParameters(Consumer<Parameter> visitor) {
		visitor.accept(Parameter.TEXT);
	}
}
