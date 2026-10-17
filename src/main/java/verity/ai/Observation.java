package verity.ai;

import java.util.List;

/**
 * An immutable snapshot of what Verity can perceive at one moment.
 * It is collected on the server thread and then safe to hand to the background thread.
 */
public record Observation(
		String mode,
		float health,
		float maxHealth,
		int lightLevel,
		String weather,
		List<PlayerSighting> players
) {
	public record PlayerSighting(
			String name,
			int distance,
			boolean lookingAtVerity,
			String heldItem
	) {
	}

	/** A short plain-text description for the model prompt. */
	public String toPromptText() {
		StringBuilder text = new StringBuilder();
		text.append("Your mode: ").append(mode)
				.append(". Your health: ").append(Math.round(health)).append('/').append(Math.round(maxHealth)).append(".\n");
		text.append("Light level here: ").append(lightLevel).append("/15 (").append(describeLight(lightLevel))
				.append("). Weather: ").append(weather).append(".\n");

		if (players.isEmpty()) {
			text.append("No players nearby.");
		} else {
			text.append("Players nearby:");
			for (PlayerSighting player : players) {
				text.append("\n- ").append(player.name())
						.append(": ").append(player.distance()).append(" blocks away, ")
						.append(player.lookingAtVerity() ? "looking at you" : "not looking at you")
						.append(", holding ").append(player.heldItem());
			}
		}
		return text.toString();
	}

	private static String describeLight(int level) {
		if (level <= 4) {
			return "dark";
		}
		if (level <= 9) {
			return "dim";
		}
		return "bright";
	}
}
