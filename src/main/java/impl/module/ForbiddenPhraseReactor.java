package impl.module;

import com.google.common.collect.Sets;
import core.OwO;
import core.handler.EventHandler;
import core.handler.IModule;
import core.handler.RegisterModule;
import core.handler.TransientEvent;
import core.i18n.Console;
import core.listener.ReactionListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static core.factory.DBConnectionFactory.connect;

@RegisterModule
public class ForbiddenPhraseReactor implements IModule {
	private static final Set<Pattern>           forbiddenPhrases = Sets.newHashSet();
	private              EventHandler.IListener listener;

	public static List<String> getPhrases() {
		return forbiddenPhrases.stream().map(Pattern::pattern).collect(Collectors.toList());
	}

	public static void addPhrase(String phrase) {
		Pattern p = Pattern.compile(phrase);
		if (!forbiddenPhrases.contains(p)) {
			forbiddenPhrases.add(p);
			final String sql = "REPLACE INTO forbiddenphrases VALUES(?);";
			try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, phrase);
				pstmt.execute();
			} catch (SQLException e) {
				OwO.logger.warn(Console.ERROR_EXCEPTION_SQL, e);
			}
		}
	}

	public static boolean removePhrase(String phrase) {
		Pattern p = Pattern.compile(phrase);
		if (forbiddenPhrases.remove(p)) {
			final String sql = "DELETE FROM forbiddenphrases WHERE phrase = ?;";
			try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, phrase);
				pstmt.execute();
			} catch (SQLException e) {
				OwO.logger.warn(Console.ERROR_EXCEPTION_SQL, e);
			}
			return true;
		}
		return false;
	}

	public void init() {
		try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
			stmt.execute("CREATE TABLE IF NOT EXISTS forbiddenphrases (" +
					"phrase TEXT" +
					");");
		} catch (SQLException e) {
			OwO.logger.warn(Console.ERROR_EXCEPTION_SQL, e);
		}
	}

	@Override
	public void enable() {
		updateFromDB();
		listener = EventHandler.addListener(EventHandler.Priority.TOP, MessageReceivedEvent.class, (EventHandler.IListener<MessageReceivedEvent>) event -> {
			if (forbiddenPhrases.stream()
					.anyMatch(p -> p.matcher(event.event.getMessage().getContent()).find())) {
				AtomicReference<IMessage> message = new AtomicReference<>(null);
				new RequestBuilder(OwO.client)
						.shouldBufferRequests(true)
						.doAction(() -> {
							event.event.getGuild().setMuteUser(event.event.getAuthor(), true);
							return true;
						})
						.doAction(() -> {
							message.set(event.event.getChannel().sendMessage(new EmbedBuilder()
									.withTitle("Notice")
									.withColor(Color.RED)
									.appendDesc(event.event.getAuthor().mention() + " has been muted for saying a forbidden phrase." +
											"\nReact with the check mark to pardon the mute." +
											"\nReact with the skull to ban immediately.")
									.build()));
							return true;
						})
						.andThen(() -> {
							message.get().addReaction(Reaction.BAN.emoji);
							return true;
						})
						.andThen(() -> {
							message.get().addReaction(Reaction.PARDON.emoji);
							return true;
						})
						.andThen(() -> {
							new ReactionListener(new ReactionListener.Builder(message.get()).setAuthor(null).setOnAdd((react) -> {
								if (react.getUser().equals(OwO.client.getOurUser()))
									return TransientEvent.ReturnType.DONOTHING;
								if (react.getUser().getPermissionsForGuild(message.get().getGuild()).contains(Permissions.ADMINISTRATOR)) {
									switch (Reaction.get(react.getReaction().getEmoji())) {
										case UNKNOWN:
											break;
										case BAN:
											oof(event.event.getMessage());
										case PARDON:
											event.event.getGuild().setMuteUser(event.event.getAuthor(), false);
											RequestBuffer.request(message.get()::delete);
											break;
									}
								}
								return TransientEvent.ReturnType.DONOTHING;
							}));
							return true;
						}).execute();
			}
			return TransientEvent.ReturnType.DONOTHING;
		});
	}

	private static void oof(IMessage message) {
		message.getChannel().sendMessage("oof'd");
		//						event.event.getGuild().banUser(event.event.getAuthor());

	}

	@Override
	public void disable() {
		EventHandler.removeListener(listener);
	}

	private static void updateFromDB() {
		final String sql = "SELECT phrase FROM forbiddenphrases";
		try (Connection conn = connect();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next())
				forbiddenPhrases.add(Pattern.compile(rs.getString("phrase")));
		} catch (SQLException e) {
			OwO.logger.warn(Console.ERROR_EXCEPTION_SQL, e);
		}
	}

	enum Reaction {
		BAN(ReactionEmoji.of("\uD83D\uDC80")),
		PARDON(ReactionEmoji.of("✅")),
		UNKNOWN(null);

		ReactionEmoji emoji;

		Reaction(ReactionEmoji emoji) {
			this.emoji = emoji;
		}

		static Reaction get(ReactionEmoji emoji) {
			for (Reaction r : Reaction.values()) {
				if (emoji.equals(r.emoji))
					return r;
			}
			return UNKNOWN;
		}
	}
}
