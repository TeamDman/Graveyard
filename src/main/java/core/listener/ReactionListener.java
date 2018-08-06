package core.listener;

import core.OwO;
import core.handler.TransientEvent;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent;
import sx.blah.discord.handle.obj.IMessage;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ReactionListener {
	private final IMessage                                                 message;
	private final Function<ReactionAddEvent, TransientEvent.ReturnType>    onAdd;
	private final Function<ReactionRemoveEvent, TransientEvent.ReturnType> onRemove;
	private final Runnable                                                 onStop;
	private       IListener                                                listenerAdd, listenerRemove;


	public ReactionListener(IMessage message, @Nullable Function<ReactionAddEvent, TransientEvent.ReturnType> onAdd, @Nullable Function<ReactionRemoveEvent, TransientEvent.ReturnType> onRemove, @Nullable Runnable onStop) {
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
			if (onAdd.apply(event) == TransientEvent.ReturnType.UNSUBSCRIBE)
				dispose();
		}
	}

	private class RemoveListener implements IListener<ReactionRemoveEvent> {
		@Override
		public void handle(ReactionRemoveEvent event) {
			if (onRemove.apply(event) == TransientEvent.ReturnType.UNSUBSCRIBE)
				dispose();
		}
	}
}
