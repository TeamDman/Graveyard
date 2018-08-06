package impl.handler;

import core.handler.DatabaseHandler;
import core.handler.EventHandler;
import core.handler.TransientEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.RequestBuffer;

import java.util.Date;

public class IdleRPGHandler {
	public static void init() {
		registerHandler();
	}

	private static void registerHandler() {
		EventHandler.addListener(MessageReceivedEvent.class, new EventHandler.Listener<MessageReceivedEvent>() {
			@Override
			public TransientEvent.ReturnType handle(TransientEvent<MessageReceivedEvent> event) {
				onMessage(event.event);
				return TransientEvent.ReturnType.DONOTHING;
			}
		});
	}

	private static void onMessage(MessageReceivedEvent event) {
		if (event.getMessage().getContent().equals(">")) {
			User user = DatabaseHandler.getUser(event.getAuthor());
			if (user != null) {
				if (deltaLevelup(user.levelup) <= 0) {
					user.levelup();
					RequestBuffer.request(()->event.getChannel().sendMessage(event.getAuthor().getName() + " has reached level " + user.level + " in idlerpg!\nNext level in " + deltaLevelup(user.levelup) + " seconds."));
					DatabaseHandler.insert(user);
				} else {
					RequestBuffer.request(() -> event.getChannel().sendMessage("You have " + deltaLevelup(user.levelup) + " seconds until your next levelup."));
				}
			} else {
				DatabaseHandler.insert(event.getAuthor().getLongID(), 1, getNextLevelup(1));
			}
		}
	}

	public static long getNextLevelup(int level) {
		return (long) ((new Date().getTime() / 1000) + (600 * (Math.pow(1.16, level))));
	}

	public static long deltaLevelup(long levelup) {
		return levelup - (new Date().getTime()/1000);
	}

	public static class User {
		public int  level;
		public long userId, levelup;

		public User(long userId, int level, long levelup) {
			this.userId = userId;
			this.level = level;
			this.levelup = levelup;
		}

		public User levelup() {
			levelup = getNextLevelup(++level);
			return this;
		}
	}
}
