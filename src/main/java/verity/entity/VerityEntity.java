package verity.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import verity.config.VerityConfig;

public class VerityEntity extends PathfinderMob {
	/** Multiplier on movement speed while wandering. Below 1 means slower than top speed. */
	private static final double WANDER_SPEED = 0.6;
	private static final float LOOK_DISTANCE = 8.0f;

	public VerityEntity(EntityType<? extends VerityEntity> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		VerityConfig config = VerityConfig.get();
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, config.maxHealth)
				.add(Attributes.MOVEMENT_SPEED, config.movementSpeed);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new RandomStrollGoal(this, WANDER_SPEED));
		this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, LOOK_DISTANCE));
		this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
	}
}
