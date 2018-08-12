package core.handler;

import com.google.common.collect.Lists;
import core.OwO;
import core.command.RegisterCommand;
import core.i18n.Console;
import eu.infomas.annotation.AnnotationDetector;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

public class ModuleHandler {
	public static final List<IModule> modules = Lists.newArrayList();

	public static void init() {
		registerModules();
		modules.forEach(IModule::init);
		modules.forEach(IModule::enable);
	}

	private static void registerModules() {
		try {
			new AnnotationDetector(new AnnotationDetector.TypeReporter() {
				@Override
				public void reportTypeAnnotation(Class<? extends Annotation> annotation, String className) {
					try {
						Class<?> clazz = Class.forName(className);
						Object   inst  = clazz.getDeclaredConstructor().newInstance();
						if (inst instanceof IModule && annotation == RegisterModule.class) {
							IModule module = ((IModule) inst);
							OwO.logger.debug(Console.DEBUG_MODULE_DISCOVERED, module);
							registerModule(module, clazz);
						}
					} catch (Throwable e) {
						OwO.logger.warn(Console.ERROR_EXCEPTION_MODULE_REGISTER,className, e);
					}
				}

				@Override
				@SuppressWarnings("unchecked")
				public Class<? extends Annotation>[] annotations() {
					return new Class[]{RegisterModule.class};
				}

			}).detect();
		} catch (IOException e) {
			OwO.logger.error(Console.ERROR_EXCEPTION_MODULE_ANNOTATION, e);
			OwO.exit(OwO.ExitLevel.ERROR);
		}
	}

	private static void registerModule(IModule module, Class clazz) {
		modules.add(module);
	}

}
