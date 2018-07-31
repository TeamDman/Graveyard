package main.Handlers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.devtools.common.options.OptionsParser;
import com.google.gson.Gson;
import eu.infomas.annotation.AnnotationDetector;
import main.Commands.obj.*;
import main.OwO;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler {
	public static final List<Command> commands = Lists.newArrayList();

	public static void registerCommands() {
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

			}).detect("main.Commands");
		} catch (IOException e) {
			OwO.logger.error("Error detecting command register annotation, aborting", e);
			OwO.exit(OwO.ExitLevel.ERROR);
		}
	}

	private static void registerCommand(Command c, Class clazz) {
		if (c.name == null)
			OwO.logger.warn("Command {} is missing a valid name. Skipping", clazz.getName());
		else if (c.commands == null || c.commands.length == 0)
			OwO.logger.warn("Command {} is missing valid commands. Skipping", c.name);
		else if (!(c instanceof IInvocable))
			OwO.logger.warn("Command {} does not have an invocation implementation. Skipping", c.name);
		else
			commands.add(c);
	}

	public static void registerListener() {
		final Pattern commandPattern = Pattern.compile("OwO\\s+(\\S+)\\s*(.*)");
		EventHandler.addListener(MessageReceivedEvent.class, new EventHandler.Listener<MessageReceivedEvent>() {
			@Override
			public TransientEvent.ReturnType handle(TransientEvent<MessageReceivedEvent> event) {
				Matcher m = commandPattern.matcher(event.event.getMessage().getContent());
				if (m.find())
					commands.stream()
							.filter(v -> Arrays.asList(v.commands).contains(m.group(1)))
							.findFirst()
							.ifPresent(c -> CommandHandler.invokeCommand(c, event.event.getMessage(), m.group(2)));
				return TransientEvent.ReturnType.DONOTHING;
			}
		});
	}

	private static void invokeCommand(Command c, IMessage msg, String body) {
		CommandArguments args = ArgumentBuilder.build(c, msg, body);
		if (args == null) {
			OwO.logger.warn("Missing arguments object invoking command {}. Skipping", c.name);
		} else if (args.options.help) {
			args.message.getChannel().sendMessage(new EmbedBuilder()
					.withTitle(c.name)
					.appendDesc(args.parser.describeOptions(Maps.newHashMap(), OptionsParser.HelpVerbosity.LONG))
					.build());
		} else {
			try {
				c.assertPermissions(args);
				if (c instanceof IInvocable)
					((IInvocable) c).invoke(args);
				else
					throw new NoSuchMethodException("Command is missing an invocation method!");
			} catch (InvalidPermissionsException e) {
				RequestBuffer.request(() -> args.message.getChannel().sendMessage(e.toString()));

			} catch (Throwable e) {
				OwO.logger.warn("Error executing command '" + c.name + "'", e);
				args.message.getChannel().sendMessage(new EmbedBuilder()
						.withTitle("Error executing command")
						.appendDesc(e.toString())
						.build()
				);
			}
		}
	}

	public static class InvalidPermissionsException extends Exception {
		public InvalidPermissionsException(String message) {
			super(message);
		}

		@Override
		public String toString() {
			String s = "InvalidPermissionsException";
			String message = getLocalizedMessage();
			return (message != null) ? (s + ": " + message) : s;
		}
	}

	public static class MissingOptionException extends Exception {
		public MissingOptionException(String message) {
			super(message);
		}

		@Override
		public String toString() {
			String s = "MissingOptionException";
			String message = getLocalizedMessage();
			return (message != null) ? (s + ": " + message) : s;
		}
	}
}