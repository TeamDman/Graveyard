package core.handler;

import core.OwO;
import impl.handler.IdleRPGHandler;
import sx.blah.discord.handle.obj.IUser;

import java.sql.*;

public class DatabaseHandler {
	//"name VARCHAR (64)" +
	public static void init() {
		try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
			stmt.execute("CREATE TABLE IF NOT EXISTS idlerpg (" +
					"userid LONG PRIMARY KEY," +
					"level INTEGER," +
					"levelup LONG" +
					");");
		} catch (SQLException e) {
			OwO.logger.warn("Exception initializing database", e);
		}
	}

	private static Connection connect() {
		String url = "jdbc:sqlite:OwO-Bot.sqlite";
		try {
			OwO.logger.info("Connected to SQLite database.");
			return DriverManager.getConnection(url);
		} catch (SQLException e) {
			OwO.logger.warn("Error connecting to SQLite database", e);
		}
		return null;
	}

	public static IdleRPGHandler.User getUser(IUser user) {
		final String sql = "SELECT level, levelup FROM idlerpg WHERE userid=?;";
		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, user.getLongID());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				return new IdleRPGHandler.User(
						user.getLongID(),
						rs.getInt("level"),
						rs.getLong("levelup")
				);
			}
		} catch (SQLException e) {
			OwO.logger.warn("Exception getting IdleRPG user", e);
		}
		return null;
	}

	public static void insert(long user, int level, long time) {
		final String sql = "REPLACE INTO idlerpg VALUES(?,?,?);";
		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, user);
			pstmt.setInt(2, level);
			pstmt.setLong(3, time);
			pstmt.execute();
		} catch (SQLException e) {
			OwO.logger.warn("Exception during SQL insert", e);
		}
	}

	public static void insert(IdleRPGHandler.User user) {
		insert(user.userId,user.level,user.levelup);
	}
}
