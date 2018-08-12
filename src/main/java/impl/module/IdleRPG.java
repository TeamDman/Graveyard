package impl.module;

import core.OwO;
import core.handler.EventHandler;
import core.handler.IModule;
import core.handler.RegisterModule;
import core.handler.TransientEvent;
import core.i18n.Console;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.sql.*;
import java.util.Date;

import static core.factory.DBConnectionFactory.connect;

@RegisterModule
public class IdleRPG implements IModule {
	private EventHandler.IListener listener;

	private static TransientEvent.ReturnType onMessage(TransientEvent<MessageReceivedEvent> event) {
		if (event.event.getMessage().getContent().equals(">")) {
			User user = getUser(event.event.getAuthor());
			if (user != null) {
				if (deltaLevelup(user.levelup) <= 0) {
					user.levelup();
					RequestBuffer.request(() -> event.event.getChannel().sendMessage(event.event.getAuthor().getName() + " has reached level " + user.level + " in IdleRPG!\nNext level in " + deltaLevelup(user.levelup) + " seconds."));
					setUser(user);
				} else {
					RequestBuffer.request(() -> event.event.getChannel().sendMessage("You have " + deltaLevelup(user.levelup) + " seconds until your next levelup."));
				}
			} else {
				setUser(event.event.getAuthor().getLongID(), 1, getNextLevelup(1));
			}
		}
		return TransientEvent.ReturnType.DONOTHING;
	}

	public static User getUser(IUser user) {
		final String sql = "SELECT level, levelup FROM idlerpg WHERE userid=?;";
		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, user.getLongID());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				return new User(
						user.getLongID(),
						rs.getInt("level"),
						rs.getLong("levelup")
				);
			}
		} catch (SQLException e) {
			OwO.logger.warn(Console.ERROR_EXCEPTION_SQL, e);
		}
		return null;
	}

	public static void setUser(User user) {
		setUser(user.userId, user.level, user.levelup);
	}

	private static void setUser(long user, int level, long time) {
		final String sql = "REPLACE INTO idlerpg VALUES(?,?,?);";
		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, user);
			pstmt.setInt(2, level);
			pstmt.setLong(3, time);
			pstmt.execute();
		} catch (SQLException e) {
			OwO.logger.warn(Console.ERROR_EXCEPTION_SQL, e);
		}
	}

	private static long getNextLevelup(int level) {
		return (long) ((new Date().getTime() / 1000) + (600 * (Math.pow(1.16, level))));
	}

	public static long deltaLevelup(long levelup) {
		return levelup - (new Date().getTime() / 1000);
	}

	public void init() {
		try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
			stmt.execute("CREATE TABLE IF NOT EXISTS idlerpg (" +
					"userid INTEGER PRIMARY KEY," +
					"level INTEGER," +
					"levelup INTEGER" +
					");");
		} catch (SQLException e) {
			OwO.logger.warn(Console.ERROR_EXCEPTION_SQL, e);
		}
	}

	@Override
	public void enable() {
		listener = EventHandler.addListener(EventHandler.Priority.BOTTOM, MessageReceivedEvent.class, IdleRPG::onMessage);
	}

	@Override
	public void disable() {
		EventHandler.removeListener(listener);
	}

	public static class User {
		public final long userId;
		public       int  level;
		public       long levelup;

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
