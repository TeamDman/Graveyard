package main.Listeners;

import main.OwO;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuilder;

import java.util.List;

public class PaginatorListener {
	private final IUser             author;
	private final IChannel          channel;
	private       int               index = 0;
	private final IMessage          message;
	private final List<EmbedObject> pages;
	private final ReactionListener  reactionListener;

	public PaginatorListener(IMessage source, List<EmbedObject> pages) {
		this.author = source.getAuthor();
		this.channel = source.getChannel();
		this.pages = pages;
		this.message = channel.sendMessage(pages.get(0));
		new RequestBuilder(OwO.client)
				.shouldBufferRequests(true)
				.doAction(() -> {
					message.addReaction(PageControl.LEFT.emoji);
					return true;
				})
				.andThen(() -> {
					message.addReaction(PageControl.RIGHT.emoji);
					return true;
				})
				.andThen(() -> {
					message.addReaction(PageControl.STOP.emoji);
					return true;
				})
				.execute();

		reactionListener = new ReactionListener(source, this::handle, this::handle, null);
	}

	private void handle(ReactionEvent event) {
		if (event.getMessageID() != message.getLongID() || !event.getUser().equals(author))
			return;
		switch (PageControl.fromEmoji(event.getReaction().getEmoji())) {
			case LEFT:
				index = --index < 0 ? pages.size() - 1 : index;
				displayPage();
				break;
			case RIGHT:
				index = ++index >= pages.size() ? 0 : index;
				displayPage();
				break;
			case STOP:
				RequestBuffer.request(message::removeAllReactions);
				reactionListener.dispose();
				break;
		}
	}

	private void displayPage() {
		RequestBuffer.request(() -> message.edit(pages.get(index)));
	}

	private enum PageControl {
		LEFT(ReactionEmoji.of("⬅")),
		RIGHT(ReactionEmoji.of("➡")),
		STOP(ReactionEmoji.of("❌")),
		UNKNOWN(null);
		final ReactionEmoji emoji;

		PageControl(ReactionEmoji emoji) {
			this.emoji = emoji;
		}

		static PageControl fromEmoji(ReactionEmoji emoji) {
			for (PageControl control : values())
				if (emoji.equals(control.emoji))
					return control;
			return UNKNOWN;
		}
	}
}
