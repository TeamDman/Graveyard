package main.impl.commands;

import com.google.devtools.common.options.Option;
import main.core.command.*;
import main.core.handler.CommandHandler;
import main.core.handler.ConversionHandler;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

@RegisterCommand
public class Say extends Command implements IInvocable<Say.Options> {
	public Say() {
		super(new Builder("Say"));
	}


	public void invoke(CommandArguments<Options> args) throws CommandHandler.InvalidOptionException {
		assertOption(!args.options.message.isEmpty(),"Message can't be empty!");
		IChannel ch = args.options.channel != null ? args.options.channel : args.message.getChannel();
		if (args.options.tts) {
			if (args.message.getAuthor().getPermissionsForGuild(args.message.getGuild()).contains(Permissions.SEND_TTS_MESSAGES))
				ch.sendMessage(args.options.message, true);
			else
				ch.sendMessage("You do not have permission to send text-to-speech messages.");
		} else {
			ch.sendMessage(args.options.message);
		}
	}

	@CommandOptions("-m ...")
	public static class Options extends OptionsDefault {
		@Option(
				name = "tts",
				abbrev = 't',
				help = "Whether or not the message should use text to speech.",
				defaultValue = "false"
		)
		public boolean tts;

		@Option(
				name= "hidden",
				abbrev = 'h',
				help = "Delete the command sender's message.",
				defaultValue = "false"
		)
		public boolean hidden;

		@Option(
				name = "channel",
				abbrev = 'c',
				help = "Specific channel to send the message to.",
				defaultValue = "-1",
				converter = ConversionHandler.ChannelConverter.class
		)
		public IChannel channel;

		@Option(
				name="message",
				abbrev = 'm',
				help="Message to send to the channel.",
				defaultValue = ""
		)
		public String message;
	}
}
