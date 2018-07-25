package main.Handlers;

import com.google.common.collect.Maps;
import com.google.devtools.common.options.OptionsParser;
import eu.infomas.annotation.AnnotationDetector;
import main.Commands.obj.ArgumentBuilder;
import main.Commands.obj.Command;
import main.Commands.obj.CommandArgument;
import main.Commands.obj.RegisterCommand;
import main.OwO;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandHandler {
	public static final Map<Command, Method> commands = Maps.newHashMap();

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
							registerCommand((Command) inst, clazz);
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
			OwO.logger.warn("Command {} is missing a valid name. Skipping", c.name);
		else if (c.commands == null || c.commands.length == 0)
			OwO.logger.warn("Command {} is missing valid commands. Skipping", c.name);
		else
			try {
				Method m = c.getClass().getDeclaredMethod("invoke", ArgumentBuilder.buildEmpty(c).getClass());
				commands.put(c, m);
			} catch (NoSuchMethodException e) {
				OwO.logger.warn("Command {} is missing an invocation method", c.name);
			}
	}

	public static Optional<Command> findCommand(String cmd) {
		return commands.keySet().stream()
				.filter(v -> Arrays.stream(v.commands).anyMatch(cmd::equals))
				.findFirst();
	}

	public static void invokeCommand(Command c, CommandArgument args) {
		if (args == null) {
			OwO.logger.warn("Missing arguments object invoking command {}. Skipping", c.name);
			return;
		}
		if (args.options.help) {
			args.message.getChannel().sendMessage(new EmbedBuilder()
					.withTitle(c.name)
					.appendDesc(args.parser.describeOptions(Maps.newHashMap(), OptionsParser.HelpVerbosity.LONG))
					.build());
		} else if (!c.hasPerms(args.message.getAuthor(), args.message.getGuild())) {
			EnumSet<Permissions> has = args.message.getAuthor().getPermissionsForGuild(args.message.getGuild());
			RequestBuffer.request(() ->
					args.message.getChannel().sendMessage("You are missing permissions for this command.\n"
							+ c.getPerms().stream()
							.filter(p -> !has.contains(p))
							.map(Enum::name)
							.collect(Collectors.joining("\n"))));
		} else {
			try {
				commands.get(c).invoke(c, args);
			} catch (InvocationTargetException | IllegalAccessException e) {
				OwO.logger.error("Error executing command '" + c.name + "'", e);
				args.message.getChannel().sendMessage(new EmbedBuilder()
						.withTitle("Error executing command")
						.appendDesc(e.toString())
						.build()
				);
			}
		}
	}
}