package main.Listeners;

import main.Commands.obj.ArgumentBuilder;
import main.Handlers.CommandHandler;
import main.OwO;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandListenerSingleton implements IListener<MessageReceivedEvent> {
	private static CommandListenerSingleton singleton;
	private        Pattern                  commandPattern = Pattern.compile("OwO\\s+(\\S+)\\s*(.*)");

	private CommandListenerSingleton() {
	}

	public static CommandListenerSingleton getSingleton() {
		if (singleton == null)
			return singleton = new CommandListenerSingleton();
		return singleton;
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		OwO.logger.debug("Received message '{}'", event.getMessage().getContent());
		Matcher m = commandPattern.matcher(event.getMessage().getContent());
		if (m.find())
			CommandHandler.findCommand(m.group(1))
					.ifPresent(c -> CommandHandler.invokeCommand(c, ArgumentBuilder.build(c, event.getMessage(), m)));

	}
}
