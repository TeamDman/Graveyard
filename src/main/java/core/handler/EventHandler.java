package core.handler;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import core.OwO;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;

public class EventHandler {
	private static final HashMap<Class<? extends Event>, ArrayDeque<IListener>> listeners = Maps.newHashMap();

	public static void init() {
		EventDispatcher dispatcher = OwO.client.getDispatcher();
		dispatcher.registerListener(onMessage.class);
	}

	public static <T extends Event> IListener<T> addListener(Priority priority, Class<? extends Event> event, IListener<T> listener) {
		listeners.computeIfAbsent(event, k -> Queues.newArrayDeque());
		if (priority == Priority.TOP)
			listeners.get(event).addFirst(listener);
		else
			listeners.get(event).addLast(listener);
		return listener;
	}

	public static void removeListener(IListener listener) {
		listeners.forEach((i,v) -> v.remove(listener));
	}

	public enum Priority {
		TOP,
		BOTTOM
	}

	public interface IListener<T extends Event> {
		TransientEvent.ReturnType handle(TransientEvent<T> event);
	}


	@SuppressWarnings("unchecked")
	public static class onMessage {
		@SuppressWarnings("SpellCheckingInspection")
		@EventSubscriber
		public static void handle(MessageReceivedEvent event) {
			Iterator<IListener>                  iter   = listeners.getOrDefault(MessageReceivedEvent.class, Queues.newArrayDeque()).iterator();
			TransientEvent<MessageReceivedEvent> tevent = new TransientEvent<>(event);
			while (iter.hasNext() && !tevent.isCanceled()) {
				IListener listener = iter.next();
				if (listener.handle(tevent) == TransientEvent.ReturnType.UNSUBSCRIBE) {
					iter.remove();
				}
			}
		}
	}
}
