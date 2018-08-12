import ch.qos.cal10n.IMessageConveyor;
import ch.qos.cal10n.MessageConveyor;
import ch.qos.cal10n.verifier.Cal10nError;
import ch.qos.cal10n.verifier.IMessageKeyVerifier;
import ch.qos.cal10n.verifier.MessageKeyVerifier;
import core.i18n.Console;
import core.i18n.LocLogger;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class LocalizationTest {
	@Test
	public void en_CA() {
		IMessageKeyVerifier mkv       = new MessageKeyVerifier(Console.class);
		List<Cal10nError>   errorList = mkv.verify(Locale.CANADA);
		errorList.forEach(System.out::println);
		assert errorList.size() == 0;
	}

	@Test
	public void CheckSubstitutions() {
		IMessageConveyor mc     = new MessageConveyor(Locale.CANADA);
		LocLogger        logger = new LocLogger(LoggerFactory.getLogger("OwO-Test"),mc);
		for (Console item : Console.values()) {
			String[] args = new String[StringUtils.countMatches(mc.getMessage(item),"{}")];
			for (int i = 0; i < args.length; i++) {
				args[i] = "yeet";
			}
			try {
				logger.debug(item, (Object[]) args);
			} catch (Throwable t) {
				System.out.println(item);
				System.out.println(mc.getMessage(item));
				System.out.println(String.join(", ",args));
				System.out.println(mc.getMessage(item,args));
				throw t;
			}
		}
		assert true;
	}
}
