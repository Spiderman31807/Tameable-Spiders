package tamable.spiders;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class ClimbMode {
	private static int mode = -3;

	public static void set(int type) {
		if (mode == type)
			return;
		mode = type;
		save();
	}

	public static int get() {
		if (mode == -3)
			load();
		return mode;
	}

	public static String texture() {
		return switch (mode) {
			default -> "";
			case 2 -> "up";
			case 0 -> "neutral";
			case -1 -> "down";
			case -2 -> "negitive";
		};
	}

	private static final Path location = Paths.get("climbing-mode.txt");

	public static void save() {
		try {
			Files.writeString(location, Integer.toString(mode));
		} catch (IOException e) {
		}
	}

	public static void load() {
		mode = 1;
		try {
			if (Files.exists(location))
				mode = Integer.parseInt(Files.readString(location).trim());
		} catch (IOException e) {
		}
	}
}