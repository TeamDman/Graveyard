package main.Listeners;

import main.OwO;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent;
import sx.blah.discord.handle.obj.IMessage;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ReactionListener {
	private IListener listenerAdd, listenerRemove;
	private final IMessage                      message;
	private final Consumer<ReactionAddEvent>    onAdd;
	private final Consumer<ReactionRemoveEvent> onRemove;
	private final Runnable                      onStop;


	public ReactionListener(IMessage message, @Nullable Consumer<ReactionAddEvent> onAdd, @Nullable Consumer<ReactionRemoveEvent> onRemove, @Nullable Runnable onStop) {
		this.message = message;
		this.onAdd = onAdd;
		this.onRemove = onRemove;
		this.onStop = onStop;
		if (onAdd != null)
			OwO.client.getDispatcher().registerListener(listenerAdd = new AddListener());
		if (onRemove != null)
			OwO.client.getDispatcher().registerListener(listenerRemove = new RemoveListener());
	}


	public void dispose() {
		if (onStop != null)
			onStop.run();
		if (listenerAdd != null)
			OwO.client.getDispatcher().unregisterListener(listenerAdd);
		if (listenerRemove != null)
			OwO.client.getDispatcher().unregisterListener(listenerRemove);
	}


	private class AddListener implements IListener<ReactionAddEvent> {
		@Override
		public void handle(ReactionAddEvent event) {
			onAdd.accept(event);
		}
	}

	private class RemoveListener implements IListener<ReactionRemoveEvent> {
		@Override
		public void handle(ReactionRemoveEvent event) {
			onRemove.accept(event);
		}
	}
}
