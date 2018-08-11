package impl.handler;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.OwO;
import core.handler.EventHandler;
import core.handler.TransientEvent;
import core.listener.ReactionListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class MTGAnywhereHandler {
	public static final  Set<Card>     cards          = Sets.newHashSet();
	private static final ReactionEmoji emoji          = ReactionEmoji.of("mtg", 477378432242679808L);
	private static final int           maxLevenshtein = 1;

	public static void init() {
		try {
			OwO.logger.info("Parsing MTG cards");
			String text = new Scanner(new File("src/main/resources/mtgcards.json")).useDelimiter("\\A").next();
			((Map<String, Expansion>) new Gson().fromJson(text, new TypeToken<Map<String, Expansion>>() {
			}.getType())).values().forEach(e -> Collections.addAll(cards, e.cards));
			cards.removeIf(card -> card.multiverseid == 0);
			for (Card c : cards)
				if (c.multiverseid == 0)
					System.out.println(c.multiverseid);
			OwO.logger.info("Parse complete");

			cards.forEach(v -> v.pattern = Pattern.compile(("(?:" + v.name
					.toLowerCase()
					.replaceAll("[^a-zA-Z ]", "")
					//					.replaceAll("(?:\\w+){1,3}", "")
					.replaceAll("\\s+", " ")
					+ ")")
					.replace("(?:)", "(?!)")));
		} catch (IOException e) {
			OwO.logger.error("Error loading mtg info", e);
		}

		EventHandler.addListener(EventHandler.Priority.BOTTOM, MessageReceivedEvent.class, (EventHandler.IListener<MessageReceivedEvent>) (event) -> {
			cards.stream().filter(x -> x.pattern.matcher(event.event.getMessage().getContent().toLowerCase()).find()).findAny().ifPresent(card -> {
				if (card.name.toLowerCase().equals("x") && !event.event.getMessage().getContent().toLowerCase().equals("x"))
					return;
				AtomicReference<IMessage> ref = new AtomicReference<>(null);
				event.event.getMessage().addReaction(emoji);
				new ReactionListener(new ReactionListener.Builder(event.event.getMessage()).setEmoji(emoji).setOnAdd((event1) -> {
					RequestBuffer.request(() -> ref.set(event.event.getChannel().sendMessage(new EmbedBuilder()
							//							.withTitle(card.name)
							//							.withUrl("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+card.multiverseid+"&type=card")
							//							.appendField("Regex", card.pattern.pattern(), true)
							.withImage("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=" + card.multiverseid + "&type=card")
							.build())));
					return TransientEvent.ReturnType.DONOTHING;
				}).setOnRemove((event1) -> {
					if (ref.get() != null) {
						new RequestBuilder(OwO.client)
								.shouldBufferRequests(true)
								.doAction(() -> {
									event.event.getMessage().removeAllReactions();
									return true;
								})
								.andThen(() -> {
									ref.get().delete();
									return true;
								})
								.execute();
					}
					return TransientEvent.ReturnType.UNSUBSCRIBE;
				}));
			});
			return TransientEvent.ReturnType.DONOTHING;
		});
	}

	static int levenshtein(String x, String y) {
		int[][] dp = new int[x.length() + 1][y.length() + 1];

		for (int i = 0; i <= x.length(); i++) {
			for (int j = 0; j <= y.length(); j++) {
				if (i == 0) {
					dp[i][j] = j;
				} else if (j == 0) {
					dp[i][j] = i;
				} else {
					dp[i][j] = min(dp[i - 1][j - 1]
									+ costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
							dp[i - 1][j] + 1,
							dp[i][j - 1] + 1);
				}
			}
		}

		return dp[x.length()][y.length()];
	}

	private static int min(int... numbers) {
		return Arrays.stream(numbers)
				.min().orElse(Integer.MAX_VALUE);
	}

	private static int costOfSubstitution(char a, char b) {
		return a == b ? 0 : 1;
	}

	private static class Card {
		int     multiverseid;
		String  name;
		Pattern pattern;

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Card && this.name.equals(((Card) obj).name);
		}
	}

	private static class Expansion {
		Card[] cards;
	}

	private static class MTGJson {
		Map<String, Card> list_of_objects;
	}
}
