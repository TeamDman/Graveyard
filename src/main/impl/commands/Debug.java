package main.impl.commands;

import com.google.devtools.common.options.Converters;
import com.google.devtools.common.options.Option;
import javafx.util.StringConverter;
import main.core.command.*;
import sx.blah.discord.util.EmbedBuilder;

@RegisterCommand
public class Debug extends Command implements IInvocable<Debug.Options> {
	public Debug() {
		super(new Builder("Debug"));
	}

	public void invoke(CommandArguments<Options> args) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.appendField("Arguments", "[" + String.join(", ", args.parser.getResidue()) + "]", false);
		embed.appendField("Options", "a=\"" + args.options.a + "\"\nb=\"" + args.options.b + "\"\nc=\"" + args.options.c + "\"\nd=\"" + args.options.d + "\"", false);
		args.message.getChannel().sendMessage(embed.build());
	}

	@CommandOptions("-a $ -b $ -c $ -d $")
	public static class Options extends OptionsDefault {
		@Option(
				name = "first",
				abbrev = 'a',
				help = "A debug flag",
				defaultValue = ""
		)
		public String a;

		@Option(
				name = "second",
				abbrev = 'b',
				help = "A debug flag",
				defaultValue = ""
		)
		public String b;

		@Option(
				name = "third",
				abbrev = 'c',
				help = "A debug flag",
				defaultValue = ""
		)
		public String c;

		@Option(
				name = "fourth",
				abbrev = 'd',
				help = "A debug flag",
				defaultValue = ""
		)
		public String d;
	}
}
