package main.Commands.obj;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

import java.util.function.Consumer;

public abstract class Command {

	public static class OptionsDefault extends OptionsBase {
		@Option(
				name="Help",
				abbrev = 'h',
				help = "Displays command information.",
				defaultValue = "false"
		)
		public boolean help;
	}

	public String name;

	public abstract Command getInstance();

	public abstract Class<? extends OptionsBase> getOptions();

	public abstract void collectParameters(Consumer<Parameter> visitor);
}
