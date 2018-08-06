package core.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.devtools.common.options.OptionsParser;
import com.google.devtools.common.options.OptionsParsingException;
import core.OwO;
import core.command.*;
import eu.infomas.annotation.AnnotationDetector;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler {
	public static final List<Command> commands = Lists.newArrayList();

	public static void init() {
		registerCommands();
		registerListener();
	}

	private static void registerCommands() {
		try {
			new AnnotationDetector(new AnnotationDetector.TypeReporter() {
				@Override
				public void reportTypeAnnotation(Class<? extends Annotation> annotation, String className) {
					try {
						Class<?> clazz = Class.forName(className);
						Object   inst  = clazz.newInstance();
						if (inst instanceof Command && annotation == RegisterCommand.class) {
							Command         command   = ((Command) inst);
							RegisterCommand registrar = ((RegisterCommand) clazz.getAnnotation(annotation));
							for (Class inner : clazz.getDeclaredClasses()) {
								if (inner.isAnnotationPresent(CommandOptions.class)) {
									command.setOptionsClass(inner);
									//									command.setSchema(((CommandOptions) inner.getAnnotation(CommandOptions.class)).value());
								}
							}
							OwO.logger.debug("Found command {} with annotation {}", inst, annotation);
							registerCommand(command, clazz);
						}
					} catch (Throwable e) {
						OwO.logger.warn("Exception registering command from " + className, e);
					}
				}

				@Override
				@SuppressWarnings("unchecked")
				public Class<? extends Annotation>[] annotations() {
					return new Class[]{RegisterCommand.class};
				}

			}).detect();
		} catch (IOException e) {
			OwO.logger.error("Error detecting command register annotation, aborting", e);
			OwO.exit(OwO.ExitLevel.ERROR);
		}
	}

	private static void registerCommand(Command c, Class clazz) {
		if (!(c instanceof IInvocable))
			OwO.logger.warn("Command {} does not have an invocation implementation. Skipping", c.getName());
		else
			commands.add(c);
	}

	private static void registerListener() {
		final Pattern commandPattern = Pattern.compile("^OwO\\s+(\\S+)\\s*(.*)");
		EventHandler.addListener(MessageReceivedEvent.class, new EventHandler.Listener<MessageReceivedEvent>() {
			@Override
			public TransientEvent.ReturnType handle(TransientEvent<MessageReceivedEvent> event) {
				Matcher m = commandPattern.matcher(event.event.getMessage().getContent());
				if (m.find())
					commands.stream()
							.filter(v -> v.getCommands().contains(m.group(1)))
							.findFirst()
							.ifPresent(c -> CommandHandler.invokeCommand(c, event.event.getMessage(), m.group(2)));
				return TransientEvent.ReturnType.DONOTHING;
			}
		});
	}

	private static void invokeCommand(Command c, IMessage msg, String body) {
		try {
			CommandArguments args = ArgumentBuilder.build(c, msg, body);
			if (args.options.help) {
				RequestBuffer.request(() -> args.message.getChannel().sendMessage(new EmbedBuilder()
						.withTitle(c.getName())
						.appendField("Options Info", args.parser.describeOptions(Maps.newHashMap(), OptionsParser.HelpVerbosity.LONG), false)
						.appendField("Options Schema", c.getSchema().length() == 0 ? "No schema" : c.getSchema(), false)
						.build()));
			} else {
				c.assertPermissions(args);
				if (c instanceof IInvocable)
					((IInvocable) c).invoke(args);
				else
					throw new NoSuchMethodException("Command is missing an invocation method!");
			}
		} catch (OptionsParsingException e) {
			OwO.logger.warn("Error getting options for command '" + c.getName() + "'", e);
			RequestBuffer.request(() -> msg.getChannel().sendMessage(new EmbedBuilder()
					.appendField("Command Option Error", e.getLocalizedMessage(), true)
					.build()));
		} catch (InvalidOptionException e) {
			RequestBuffer.request(() -> msg.getChannel().sendMessage(new EmbedBuilder()
					.appendField("InvalidOptionException", e.getMessage(), false)
					.appendDesc("See `" + c.getName().toLowerCase() + " --help` for usage details.")
					.build()));
		} catch (InvalidPermissionsException e) {
			RequestBuffer.request(() -> msg.getChannel().sendMessage(e.toString()));
		} catch (Throwable e) {
			OwO.logger.warn("Error executing command '" + c.getName() + "'", e);
			RequestBuffer.request(() -> msg.getChannel().sendMessage(new EmbedBuilder()
					.withTitle("Error executing command")
					.appendDesc(e.toString())
					.build()));
		}
	}

	public static class InvalidOptionException extends Exception {
		public InvalidOptionException(String message) {
			super(message);
		}

		@Override
		public String toString() {
			String s       = "InvalidOptionException";
			String message = getLocalizedMessage();
			return (message != null) ? (s + ": " + message) : s;
		}
	}

	public static class InvalidPermissionsException extends Exception {
		public InvalidPermissionsException(String message) {
			super(message);
		}

		@Override
		public String toString() {
			String s       = "InvalidPermissionsException";
			String message = getLocalizedMessage();
			return (message != null) ? (s + ": " + message) : s;
		}
	}
}