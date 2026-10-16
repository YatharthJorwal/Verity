package verity.entity.goal;

import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import verity.entity.VerityEntity;
import verity.entity.VerityMode;

/**
 * Follows the nearest player at a distance while in FOLLOW mode.
 * Starts walking when the player is farther than START_DISTANCE and stops inside STOP_DISTANCE.
 */
public class FollowPlayerGoal extends Goal {
	private static final double SEARCH_RANGE = 24.0;
	private static final double GIVE_UP_RANGE = 32.0;
	private static final float START_DISTANCE = 5.0f;
	private static final float STOP_DISTANCE = 3.0f;
	private static final int PATH_REFRESH_TICKS = 10;

	private final VerityEntity verity;
	private final double speedModifier;
	private Player target;
	private int refreshTimer;

	public FollowPlayerGoal(VerityEntity verity, double speedModifier) {
		this.verity = verity;
		this.speedModifier = speedModifier;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		if (this.verity.getMode() != VerityMode.FOLLOW) {
			return false;
		}
		Player nearest = this.verity.level().getNearestPlayer(this.verity, SEARCH_RANGE);
		if (nearest == null || this.verity.distanceTo(nearest) <= START_DISTANCE) {
			return false;
		}
		this.target = nearest;
		return true;
	}

	@Override
	public boolean canContinueToUse() {
		return this.verity.getMode() == VerityMode.FOLLOW
				&& this.target != null
				&& this.target.isAlive()
				&& this.verity.distanceTo(this.target) > STOP_DISTANCE
				&& this.verity.distanceTo(this.target) < GIVE_UP_RANGE;
	}

	@Override
	public void start() {
		this.refreshTimer = 0;
	}

	@Override
	public void tick() {
		if (this.target != null && --this.refreshTimer <= 0) {
			this.refreshTimer = PATH_REFRESH_TICKS;
			this.verity.getNavigation().moveTo(this.target, this.speedModifier);
		}
	}

	@Override
	public void stop() {
		this.target = null;
		this.verity.getNavigation().stop();
	}
}
