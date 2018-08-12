package core.i18n;

import ch.qos.cal10n.IMessageConveyor;
import ch.qos.cal10n.MessageParameterObj;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class LocLogger extends LoggerWrapper implements Logger {

	private static final String           FQCN      = LocLogger.class.getName();
	/**
	 * Every localized message logged by a LocLogger will bear this marker. It
	 * allows marker-aware implementations to perform additional processing on
	 * localized messages.
	 */
	static               Marker           LOCALIZED = MarkerFactory.getMarker("LOCALIZED");
	final                IMessageConveyor imc;

	public LocLogger(Logger logger, IMessageConveyor imc) {
		super(logger, LoggerWrapper.class.getName());
		if (imc == null) {
			throw new IllegalArgumentException("IMessageConveyor cannot be null");
		}
		this.imc = imc;
	}

	/**
	 * Log a localized message at the TRACE level.
	 *
	 * @param key  the key used for localization
	 * @param args optional arguments
	 */
	public void trace(Enum<?> key, Object... args) {
		if (!logger.isTraceEnabled()) {
			return;
		}
		String              translatedMsg = imc.getMessage(key, args);
		MessageParameterObj mpo           = new MessageParameterObj(key, args);

		if (instanceofLAL) {
			((LocationAwareLogger) logger).log(LOCALIZED, FQCN, LocationAwareLogger.TRACE_INT, translatedMsg, args, null);
		} else {
			logger.trace(LOCALIZED, translatedMsg, mpo);
		}
	}

	/**
	 * Log a localized message at the DEBUG level.
	 *
	 * @param key  the key used for localization
	 * @param args optional arguments
	 */
	public void debug(Enum<?> key, Object... args) {
		if (!logger.isDebugEnabled()) {
			return;
		}
		String              translatedMsg = imc.getMessage(key, args);
		MessageParameterObj mpo           = new MessageParameterObj(key, args);

		if (instanceofLAL) {
			((LocationAwareLogger) logger).log(LOCALIZED, FQCN, LocationAwareLogger.DEBUG_INT, translatedMsg, args, null);
		} else {
			logger.debug(LOCALIZED, translatedMsg, mpo);
		}
	}

	/**
	 * Log a localized message at the INFO level.
	 *
	 * @param key  the key used for localization
	 * @param args optional arguments
	 */
	public void info(Enum<?> key, Object... args) {
		if (!logger.isInfoEnabled()) {
			return;
		}
		String              translatedMsg = imc.getMessage(key, args);
		MessageParameterObj mpo           = new MessageParameterObj(key, args);

		if (instanceofLAL) {
			((LocationAwareLogger) logger).log(LOCALIZED, FQCN, LocationAwareLogger.INFO_INT, translatedMsg, args, null);
		} else {
			logger.info(LOCALIZED, translatedMsg, mpo);
		}
	}

	/**
	 * Log a localized message at the WARN level.
	 *
	 * @param key  the key used for localization
	 * @param args optional arguments
	 */
	public void warn(Enum<?> key, Object... args) {
		if (!logger.isWarnEnabled()) {
			return;
		}
		String              translatedMsg = imc.getMessage(key, args);
		MessageParameterObj mpo           = new MessageParameterObj(key, args);

		if (instanceofLAL) {
			((LocationAwareLogger) logger).log(LOCALIZED, FQCN, LocationAwareLogger.WARN_INT, translatedMsg, args, null);
		} else {
			logger.warn(LOCALIZED, translatedMsg, mpo);
		}
	}

	/**
	 * Log a localized message at the ERROR level.
	 *
	 * @param key  the key used for localization
	 * @param args optional arguments
	 */
	public void error(Enum<?> key, Object... args) {
		if (!logger.isErrorEnabled()) {
			return;
		}
		String              translatedMsg = imc.getMessage(key, args);
		MessageParameterObj mpo           = new MessageParameterObj(key, args);

		if (instanceofLAL) {
			((LocationAwareLogger) logger).log(LOCALIZED, FQCN, LocationAwareLogger.ERROR_INT, translatedMsg, args, null);
		} else {
			logger.error(LOCALIZED, translatedMsg, mpo);
		}
	}
}
