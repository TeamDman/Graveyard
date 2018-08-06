package main.core.handler;

import main.core.OwO;
import sx.blah.discord.handle.impl.obj.User;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.sql.*;
import java.util.Date;

public class DatabaseHandler {
	//"name VARCHAR (64)" +
	public static void init() {
		try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
			stmt.execute("CREATE TABLE IF NOT EXISTS idlerpg (" +
					"userid LONG," +
					"guildid LONG," +
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
			OwO.logger.warn("Error connecting to SQLite databse", e);
		}
		return null;
	}

	public static void insert(IUser user, IGuild guild) {
		final String sql = "INSERT INTO idlerpg VALUES(?,?,?,?)";
		try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, user.getLongID());
			pstmt.setLong(2, guild.getLongID());
			pstmt.setInt(3, 0);
			pstmt.setLong(4, new Date().getTime());
			pstmt.execute();
		} catch (SQLException e) {
			OwO.logger.warn("Exception during SQL insert", e);
		}
	}
}
