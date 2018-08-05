package main.impl.commands;

import com.google.devtools.common.options.Option;
import main.core.command.Command;
import main.core.command.CommandArguments;
import main.core.command.IInvocable;
import main.core.command.RegisterCommand;
import sx.blah.discord.util.EmbedBuilder;

@RegisterCommand
public class Debug extends Command implements IInvocable<Debug.Options> {
	public Debug() {
		super(new Builder("Debug").withOptions(Options.class));
	}

	public void invoke(CommandArguments<Options> args) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.appendField("Arguments", "[" + String.join(", ", args.parser.getResidue()) + "]", false);
		embed.appendField("Options", "a=\"" + args.options.a + "\"\nb=\"" + args.options.b + "\"\nc=\"" + args.options.c + "\"\nd=\"" + args.options.d + "\"", false);
		args.message.getChannel().sendMessage(embed.build());
	}

	public static class Options extends OptionsDefault {
		@Option(
				name = "First",
				abbrev = 'a',
				help = "A debug flag",
				defaultValue = ""
		)
		public String a;

		@Option(
				name = "Second",
				abbrev = 'b',
				help = "A debug flag",
				defaultValue = ""
		)
		public String b;

		@Option(
				name = "Third",
				abbrev = 'c',
				help = "A debug flag",
				defaultValue = ""
		)
		public String c;

		@Option(
				name = "Fourth",
				abbrev = 'd',
				help = "A debug flag",
				defaultValue = ""
		)
		public String d;
	}
}
