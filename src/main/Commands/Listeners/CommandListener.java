package main.Commands.Listeners;

import main.Commands.obj.ArgumentBuilder;
import main.Handlers.CommandHandler;
import main.OwO;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandListener implements IListener<MessageReceivedEvent> {
	private Pattern commandPattern = Pattern.compile("OwO\\s+(\\S+)(.*)");

	@Override
	public void handle(MessageReceivedEvent event) {
		Matcher m = commandPattern.matcher(event.getMessage().getContent());
		if (m.find()) {
			OwO.logger.info(event.getMessage().getContent());
			CommandHandler.findCommand(m.group(1)).ifPresent(c -> c.invoke(ArgumentBuilder.build(c, event.getMessage())));
		}
	}
}
