package main.Commands;

import com.google.devtools.common.options.Option;
import main.Commands.obj.Command;
import main.Commands.obj.CommandArgument;
import main.Commands.obj.RegisterCommand;
import sx.blah.discord.handle.obj.Permissions;

@RegisterCommand(name = "Say", cmds = {"say"})
public class Say extends Command {
	@SuppressWarnings("unused")
	public void invoke(CommandArgument<Options> args) {
		if (args.options.tts) {
			if (args.message.getAuthor().getPermissionsForGuild(args.message.getGuild()).contains(Permissions.SEND_TTS_MESSAGES))
				args.message.getChannel().sendMessage(String.join(" ", args.parser.getResidue()), true);
			else
				args.message.getChannel().sendMessage("You do not have permission to send text-to-speech messages.");
		} else {
			args.message.getChannel().sendMessage(String.join(" ", args.parser.getResidue()));
		}
	}

	@Override
	public Class<? extends OptionsDefault> getOptions() {
		return Options.class;
	}

	public static class Options extends OptionsDefault {
		@Option(
				name = "tts",
				abbrev = 't',
				help = "Whether or not the message should use text to speech.",
				defaultValue = "false"
		)
		public boolean tts;
	}
}
