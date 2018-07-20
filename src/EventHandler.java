import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

class EventHandler {

	@SuppressWarnings("unused")
	static class AnnotationListener {
		@EventSubscriber
		public void onReadyEvent(ReadyEvent event) {
			System.out.println(event);
		}

		@EventSubscriber
		public void onMessageReceivedEvent(MessageReceivedEvent event) {
			System.out.println(event);
		}
	}

	static class InterfaceListener implements IListener<ReadyEvent> {
		@Override
		public void handle(ReadyEvent event) {
			System.out.println(event);
		}
	}

	static void init() {
		EventDispatcher dispatcher = OwO.client.getDispatcher();
		dispatcher.registerListener(new InterfaceListener());
		dispatcher.registerListener(new AnnotationListener());
	}
}
