package verity.entity;

import java.util.Locale;

/**
 * What Verity is currently doing. Goals check this to decide whether they may run.
 * Later, validated model actions (stay, follow) will set this same value.
 */
public enum VerityMode {
	WANDER,
	FOLLOW,
	STAY;

	public String id() {
		return name().toLowerCase(Locale.ROOT);
	}

	public static VerityMode fromId(String id) {
		for (VerityMode mode : values()) {
			if (mode.id().equals(id)) {
				return mode;
			}
		}
		return WANDER;
	}
}
