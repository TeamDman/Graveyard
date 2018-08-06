package core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	private final String     name;
	private final Properties props = new Properties();

	private Config(String name) {
		this.name = name;
		load();
	}

	private void load() {
		try (FileInputStream in = new FileInputStream(name + ".properties")) {
			props.load(in);
		} catch (FileNotFoundException eNF) { // Create config from defaults
			for (OwOProperty p : OwOProperty.values())
				props.put(p.name(), p.fallback);
			save();
		} catch (IOException eIO) {
			OwO.logger.error("Unhandled error creating config object '" + name + "'", eIO);
		}
	}

	private void save() {
		try (FileOutputStream out = new FileOutputStream(name + ".properties")) {
			props.store(out, name);
		} catch (IOException e) {
			OwO.logger.error("Error while saving properties for '" + name + "'", e);
		}
	}

	static Config getConfig() {
		if (OwO.config == null) {
			return new Config("OwO-Bot");
		}
		return OwO.config;
	}

	public <T> T get(OwOProperty key) {
		//noinspection unchecked
		return (T) props.get(key.name()); // Not good, but good enough for now.
	}

	public enum OwOProperty {
		DISCORD_TOKEN("undefined");
		final Object fallback;

		OwOProperty(Object fallback) {
			this.fallback = fallback;
		}
	}
}
