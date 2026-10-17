package verity.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

import verity.config.VerityConfig;
import verity.entity.VerityEntity;

/**
 * What happens when Verity dies: a few random events aimed at a player.
 * Target: the player who killed it, otherwise the nearest player within range.
 * Controlled by config: deathEventsEnabled and deathEventCount.
 */
public final class VerityDeathEvents {
	private static final double FALLBACK_RANGE = 32.0;
	private static final int EFFECT_SECONDS = 15;

	private interface DeathEvent {
		void run(ServerLevel level, ServerPlayer target);
	}

	private static final List<DeathEvent> EVENTS = List.of(
			VerityDeathEvents::igniteTarget,
			VerityDeathEvents::lightningStrike,
			VerityDeathEvents::randomEffect
	);

	private static final List<Holder<MobEffect>> EFFECTS = List.of(
			MobEffects.POISON,
			MobEffects.WITHER,
			MobEffects.BLINDNESS,
			MobEffects.NAUSEA,
			MobEffects.SLOWNESS,
			MobEffects.WEAKNESS,
			MobEffects.HUNGER
	);

	private VerityDeathEvents() {
	}

	public static void trigger(ServerLevel level, VerityEntity verity, DamageSource source) {
		VerityConfig config = VerityConfig.get();
		if (!config.deathEventsEnabled) {
			return;
		}

		ServerPlayer target = findTarget(level, verity, source);
		if (target == null) {
			return;
		}

		List<DeathEvent> pool = new ArrayList<>(EVENTS);
		Collections.shuffle(pool);
		int count = Math.min(config.deathEventCount, pool.size());
		for (int i = 0; i < count; i++) {
			pool.get(i).run(level, target);
		}
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
		target.igniteForSeconds(8.0f);
		BlockPos feet = target.blockPosition();
		if (level.getBlockState(feet).isAir()) {
			level.setBlockAndUpdate(feet, Blocks.FIRE.defaultBlockState());
		}
	}

	private static void lightningStrike(ServerLevel level, ServerPlayer target) {
		EntityType.LIGHTNING_BOLT.spawn(level, target.blockPosition(), EntitySpawnReason.TRIGGERED);
	}

	private static void randomEffect(ServerLevel level, ServerPlayer target) {
		Holder<MobEffect> effect = EFFECTS.get(target.getRandom().nextInt(EFFECTS.size()));
		target.addEffect(new MobEffectInstance(effect, EFFECT_SECONDS * 20, 1));
	}
}
