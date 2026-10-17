package verity.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import net.fabricmc.loader.api.FabricLoader;

import verity.Verity;

/**
 * Settings loaded from config/verity.json when the game starts.
 * Changes to the file take effect after a restart.
 */
public class VerityConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static VerityConfig instance = new VerityConfig();

	public double maxHealth = 10.0;
	public double movementSpeed = 0.2;
	/** When Verity dies, one random event is aimed at a player. */
	public boolean deathEventsEnabled = true;

	public static VerityConfig get() {
		return instance;
	}

	public static void load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("verity.json");
		VerityConfig loaded = null;
		boolean fileIsBroken = false;

		if (Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				loaded = GSON.fromJson(reader, VerityConfig.class);
			} catch (IOException | JsonParseException e) {
				fileIsBroken = true;
				Verity.LOGGER.warn("Could not read {}. Using default settings and leaving the file untouched.", path, e);
			}
		}

		if (loaded == null) {
			loaded = new VerityConfig();
		}
		loaded.sanitize();
		instance = loaded;

		if (!fileIsBroken) {
			save(path);
		}
	}

	private static void save(Path path) {
		try (Writer writer = Files.newBufferedWriter(path)) {
			GSON.toJson(instance, writer);
		} catch (IOException e) {
			Verity.LOGGER.warn("Could not write {}", path, e);
		}
	}

	private void sanitize() {
		maxHealth = Double.isNaN(maxHealth) ? 10.0 : Math.clamp(maxHealth, 1.0, 1000.0);
		movementSpeed = Double.isNaN(movementSpeed) ? 0.2 : Math.clamp(movementSpeed, 0.05, 1.0);
	}
}
