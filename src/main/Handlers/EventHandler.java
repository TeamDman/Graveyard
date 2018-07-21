package main.Handlers;

import main.Commands.Listeners.CommandListener;
import main.OwO;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;

public class EventHandler {

	@SuppressWarnings("unused")
	static class AnnotationListener {
		@EventSubscriber
		public void onReadyEvent(ReadyEvent event) {
//			System.out.println(event);
		}
	}

	public static void init() {
		EventDispatcher dispatcher = OwO.client.getDispatcher();
		dispatcher.registerListener(new CommandListener());
		dispatcher.registerListener(new AnnotationListener());
	}
}
