package main.core.handler;

import main.core.OwO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {

	public static void init() {
		try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
			stmt.execute("CREATE TABLE IF NOT EXISTS guildusers (" +
					"id INTEGER," +
					"guildid INTEGER" +
					"name VARCHAR (64)" +
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
}
