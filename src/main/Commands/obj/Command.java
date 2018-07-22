package main.Commands.obj;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

public abstract class Command {

	public String name;
	public String[] commands;

	public Class<? extends OptionsDefault> getOptions() {
		return OptionsDefault.class;
	}

	public static class OptionsDefault extends OptionsBase {
		@Option(
				name = "help",
				abbrev = '?',
				help = "Displays command information.",
				defaultValue = "false"
		)
		public boolean help;
	}
}
