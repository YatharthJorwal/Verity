package verity.ai;

import java.util.Comparator;
import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import verity.entity.VerityEntity;

/**
 * Builds an Observation for one Verity. Must be called on the server thread,
 * because it reads live world state.
 */
public final class ObservationCollector {
	private static final double PLAYER_RANGE = 24.0;
	private static final int MAX_PLAYERS = 4;
	/** How closely a player must aim at Verity to count as looking at it (1.0 is dead center). */
	private static final double LOOKING_THRESHOLD = 0.95;

	private ObservationCollector() {
	}

	public static Observation collect(VerityEntity verity) {
		if (!(verity.level() instanceof ServerLevel level)) {
			throw new IllegalStateException("Observations can only be collected on the server");
		}

		List<Observation.PlayerSighting> players = level.players().stream()
				.filter(player -> !player.isSpectator())
				.filter(player -> verity.distanceTo(player) <= PLAYER_RANGE)
				.sorted(Comparator.comparingDouble(player -> verity.distanceTo(player)))
				.limit(MAX_PLAYERS)
				.map(player -> sight(verity, player))
				.toList();

		return new Observation(
				verity.getMode().id(),
				verity.getHealth(),
				verity.getMaxHealth(),
				level.getMaxLocalRawBrightness(verity.blockPosition()),
				level.isThundering() ? "thunderstorm" : level.isRaining() ? "raining" : "clear",
				players
		);
	}

	private static Observation.PlayerSighting sight(VerityEntity verity, ServerPlayer player) {
		Vec3 toVerity = verity.getEyePosition().subtract(player.getEyePosition()).normalize();
		boolean looking = player.getLookAngle().dot(toVerity) >= LOOKING_THRESHOLD;
		return new Observation.PlayerSighting(
				player.getName().getString(),
				Math.round(verity.distanceTo(player)),
				looking,
				describe(player.getMainHandItem())
		);
	}

	private static String describe(ItemStack stack) {
		if (stack.isEmpty()) {
			return "nothing";
		}
		String name = stack.getHoverName().getString();
		return stack.getCount() > 1 ? name + " x" + stack.getCount() : name;
	}
}
