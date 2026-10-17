package verity.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import verity.config.VerityConfig;
import verity.entity.goal.FollowPlayerGoal;
import verity.entity.goal.ModeStrollGoal;
import verity.entity.goal.StayGoal;
import verity.event.VerityDeathEvents;

public class VerityEntity extends PathfinderMob {
	/** Multiplier on movement speed while wandering. Below 1 means slower than top speed. */
	private static final double WANDER_SPEED = 0.6;
	/** Multiplier on movement speed while following a player. */
	private static final double FOLLOW_SPEED = 0.9;
	private static final float LOOK_DISTANCE = 8.0f;

	private VerityMode mode = VerityMode.WANDER;
	private boolean deathEventsFired;

	public VerityEntity(EntityType<? extends VerityEntity> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		VerityConfig config = VerityConfig.get();
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, config.maxHealth)
				.add(Attributes.MOVEMENT_SPEED, config.movementSpeed);
	}

	public VerityMode getMode() {
		return this.mode;
	}

	public void setMode(VerityMode mode) {
		if (this.mode != mode) {
			this.mode = mode;
			this.getNavigation().stop();
		}
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new StayGoal(this));
		this.goalSelector.addGoal(2, new FollowPlayerGoal(this, FOLLOW_SPEED));
		this.goalSelector.addGoal(3, new ModeStrollGoal(this, WANDER_SPEED));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, LOOK_DISTANCE));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
	}

	/** Verity never despawns because the player is far away. */
	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		if (!this.deathEventsFired && this.level() instanceof ServerLevel serverLevel) {
			this.deathEventsFired = true;
			VerityDeathEvents.trigger(serverLevel, this, source);
		}
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putString("verity_mode", this.mode.id());
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		this.mode = VerityMode.fromId(input.getString("verity_mode").orElse("wander"));
	}
}
