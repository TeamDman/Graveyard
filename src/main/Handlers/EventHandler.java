package main.Handlers;

import main.Listeners.CommandListenerSingleton;
import main.OwO;
import sx.blah.discord.api.events.EventDispatcher;

public class EventHandler {

	//	@SuppressWarnings("unused")
	//	static class AnnotationListener {
	//		@EventSubscriber
	//		public void onReadyEvent(ReadyEvent event) {
	//			System.out.println(event);
	//		}
	//	}
	//		dispatcher.registerListener(new AnnotationListener());


	public static void init() {
		EventDispatcher dispatcher = OwO.client.getDispatcher();
		dispatcher.registerListener(CommandListenerSingleton.getSingleton());
	}
}
