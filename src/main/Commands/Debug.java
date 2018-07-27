package main.Commands;

import com.google.devtools.common.options.Option;
import main.Commands.obj.Command;
import main.Commands.obj.CommandArguments;
import main.Commands.obj.IInvocable;
import main.Commands.obj.RegisterCommand;
import sx.blah.discord.util.EmbedBuilder;

@RegisterCommand
public class Debug extends Command implements IInvocable<Debug.Options> {
	public Debug() {
		super("Debug", "debug", Options.class, null);
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
