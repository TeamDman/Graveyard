package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	private final String     name;
	private final Properties props = new Properties();
	private final ConfigType type;

	static Config getGlobalConfig() {
		if (OwO.config == null) {
			return new Config("global", ConfigType.GLOBAL);
		}
		return OwO.config;
	}

	private Config(String name, ConfigType type) {
		this.name = name;
		this.type = type;
		load();
		save();
	}

	public <T> T get(Object key) {
		//noinspection unchecked
		return (T) props.get(key); // Not good, but good enough for now.
	}

	private void load() {
		try (FileInputStream in = new FileInputStream("./properties/" + name + ".properties")) {
			props.load(in);
		} catch (FileNotFoundException eNF) { // Create config from defaults
			try (FileInputStream ind = new FileInputStream(type.getDefaultsFile())) {
				props.load(ind);
			} catch (IOException die) { // Failed loading defaults
				OwO.logger.error("Failed to load default config file creating '{}'", name);
				die.printStackTrace();
				OwO.exit();
			}
		} catch (IOException eIO) {
			OwO.logger.error("Unprecedented error creating config object '{}'", name);
			eIO.printStackTrace();
		}
	}

	private void save() {
		try (FileOutputStream out = new FileOutputStream("./properties/" + name + ".properties")) {
			props.store(out, type.name());
		} catch (IOException e) {
			OwO.logger.error("Error while saving properties for '{}'", name);
			e.printStackTrace();
		}
	}

	enum ConfigType {
		GLOBAL;

		String getDefaultsFile() {
			return "./properties/" + values()[ordinal()].toString().toLowerCase() + "-default.properties";
		}
	}
}
