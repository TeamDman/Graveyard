package core.factory;

import core.OwO;
import core.i18n.Console;
import impl.module.IdleRPG;
import sx.blah.discord.handle.obj.IUser;

import java.sql.*;

public class DBConnectionFactory {
	//"name VARCHAR (64)" +
	public static Connection connect() {
		String url = "jdbc:sqlite:OwO-Bot.sqlite";
		try {
			OwO.logger.info(Console.INFO_SQL_CONNECTED);
			return DriverManager.getConnection(url);
		} catch (SQLException e) {
			OwO.logger.warn(Console.ERROR_EXCEPTION_SQL_CONNECT, e);
		}
		return null;
	}

}
