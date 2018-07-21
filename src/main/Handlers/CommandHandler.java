package main.Handlers;

import eu.infomas.annotation.AnnotationDetector;
import main.Commands.obj.Command;
import main.Commands.obj.CommandArgument;
import main.Commands.obj.RegisterCommand;
import main.OwO;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Optional;

public class CommandHandler {
	public static final ArrayList<Command> commands = new ArrayList<>();

	public static void registerCommands() {
		try {
			new AnnotationDetector(new AnnotationDetector.TypeReporter() {
				@Override
				public void reportTypeAnnotation(Class<? extends Annotation> annotation, String className) {
					try {
						Class<?> clazz = Class.forName(className);
						Object   inst  = clazz.newInstance();
						if (inst instanceof Command) {
							OwO.logger.debug("Found command {}", inst);
							registerCommand((Command) inst);
						}
					} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
						e.printStackTrace();
					}
				}

				@Override
				@SuppressWarnings("unchecked")
				public Class<? extends Annotation>[] annotations() {
					return new Class[]{RegisterCommand.class};
				}

			}).detect("main.Commands");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void registerCommand(Command c) {
		commands.add(c);
	}

	public static Optional<Command> findCommand(String name) {
		return commands.stream()
				.filter(v -> v.name.toLowerCase().equals(name.toLowerCase()))
				.findFirst();
	}

	public static void invokeCommand(Command c, CommandArgument args){
		try {
			c.getClass().getDeclaredMethod("invoke",args.getClass()).invoke(c,args);
		} catch (NoSuchMethodException|InvocationTargetException|IllegalAccessException e) {
			OwO.logger.error("Error executing command {}",c);
			e.printStackTrace();
		}
	}
}
