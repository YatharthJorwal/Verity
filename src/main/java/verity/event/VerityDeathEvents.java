package verity.event;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

import verity.Verity;
import verity.config.VerityConfig;
import verity.entity.VerityEntity;

/**
 * What happens when Verity dies: exactly one random event aimed at a player.
 * Target: the player who killed it, otherwise the nearest player within range.
 * The aim is to startle and threaten, not to kill. Risky events are skipped when the target's health is low.
 * Controlled by config: deathEventsEnabled.
 */
public final class VerityDeathEvents {
	private static final double FALLBACK_RANGE = 32.0;

	private static final float IGNITE_MIN_HEALTH = 10.0f;
	private static final float IGNITE_SECONDS = 5.0f;

	private static final float LAVA_MIN_HEALTH = 14.0f;
	private static final int LAVA_TICKS = 60;

	private static final float LIGHTNING_DAMAGE = 4.0f;
	private static final float LIGHTNING_MIN_REMAINING_HEALTH = 2.0f;

	private record DeathEvent(
			String name,
			BiPredicate<ServerLevel, ServerPlayer> eligible,
			BiConsumer<ServerLevel, ServerPlayer> action
	) {
	}

	private record EffectChoice(Holder<MobEffect> effect, int minSeconds, int maxSeconds, int maxAmplifier) {
	}

	private static final List<DeathEvent> EVENTS = List.of(
			new DeathEvent("ignite", (level, target) -> target.getHealth() >= IGNITE_MIN_HEALTH, VerityDeathEvents::igniteTarget),
			new DeathEvent("lightning", (level, target) -> true, VerityDeathEvents::weakLightning),
			new DeathEvent("lava", VerityDeathEvents::canPlaceLava, VerityDeathEvents::lavaAtFeet),
			new DeathEvent("effect", (level, target) -> true, VerityDeathEvents::randomEffect)
	);

	private static final List<EffectChoice> EFFECTS = List.of(
			new EffectChoice(MobEffects.POISON, 8, 15, 1),
			new EffectChoice(MobEffects.WITHER, 4, 7, 0),
			new EffectChoice(MobEffects.BLINDNESS, 8, 15, 0),
			new EffectChoice(MobEffects.NAUSEA, 10, 20, 0),
			new EffectChoice(MobEffects.SLOWNESS, 8, 15, 2),
			new EffectChoice(MobEffects.WEAKNESS, 15, 30, 1),
			new EffectChoice(MobEffects.HUNGER, 10, 25, 1),
			new EffectChoice(MobEffects.MINING_FATIGUE, 15, 30, 1),
			new EffectChoice(MobEffects.DARKNESS, 10, 20, 0)
	);

	private VerityDeathEvents() {
	}

	public static void trigger(ServerLevel level, VerityEntity verity, DamageSource source) {
		if (!VerityConfig.get().deathEventsEnabled) {
			return;
		}

		ServerPlayer target = findTarget(level, verity, source);
		if (target == null) {
			return;
		}

		List<DeathEvent> eligible = EVENTS.stream()
				.filter(event -> event.eligible().test(level, target))
				.toList();
		if (eligible.isEmpty()) {
			return;
		}

		DeathEvent chosen = eligible.get(target.getRandom().nextInt(eligible.size()));
		Verity.LOGGER.info("Verity death event: {} on {}", chosen.name(), target.getName().getString());
		chosen.action().accept(level, target);
	}

	private static ServerPlayer findTarget(ServerLevel level, VerityEntity verity, DamageSource source) {
		Entity attacker = source.getEntity();
		if (attacker instanceof ServerPlayer killer) {
			return killer;
		}
		Player nearest = level.getNearestPlayer(verity, FALLBACK_RANGE);
		return nearest instanceof ServerPlayer serverPlayer ? serverPlayer : null;
	}

	private static void igniteTarget(ServerLevel level, ServerPlayer target) {
		target.igniteForSeconds(IGNITE_SECONDS);
		BlockPos feet = target.blockPosition();
		if (level.getBlockState(feet).isAir()) {
			level.setBlockAndUpdate(feet, Blocks.FIRE.defaultBlockState());
		}
	}

	/** A real-looking bolt with sound and flash, but no fire and no lethal damage. */
	private static void weakLightning(ServerLevel level, ServerPlayer target) {
		LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
		if (bolt != null) {
			bolt.snapTo(target.position());
			bolt.setVisualOnly(true);
			level.addFreshEntity(bolt);
		}
		float damage = Math.min(LIGHTNING_DAMAGE, target.getHealth() - LIGHTNING_MIN_REMAINING_HEALTH);
		if (damage > 0.0f) {
			target.hurtServer(level, level.damageSources().lightningBolt(), damage);
		}
	}

	private static boolean canPlaceLava(ServerLevel level, ServerPlayer target) {
		return target.getHealth() >= LAVA_MIN_HEALTH && level.getBlockState(target.blockPosition()).isAir();
	}

	/** Lava appears at the player's feet for a few seconds, then the original block returns. */
	private static void lavaAtFeet(ServerLevel level, ServerPlayer target) {
		TemporaryBlocks.place(level, target.blockPosition(), Blocks.LAVA.defaultBlockState(), LAVA_TICKS);
	}

	private static void randomEffect(ServerLevel level, ServerPlayer target) {
		RandomSource random = target.getRandom();
		EffectChoice choice = EFFECTS.get(random.nextInt(EFFECTS.size()));
		int seconds = choice.minSeconds() + random.nextInt(choice.maxSeconds() - choice.minSeconds() + 1);
		int amplifier = random.nextInt(choice.maxAmplifier() + 1);
		target.addEffect(new MobEffectInstance(choice.effect(), seconds * 20, amplifier));
	}
}
