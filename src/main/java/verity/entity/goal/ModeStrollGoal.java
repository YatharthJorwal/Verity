package verity.entity.goal;

import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import verity.entity.VerityEntity;
import verity.entity.VerityMode;

/**
 * Slow random wandering, only while Verity is in WANDER mode.
 * Self-contained on purpose: it does not depend on vanilla's idle-time checks.
 */
public class ModeStrollGoal extends Goal {
	/** On average Verity picks a new spot once per this many idle ticks. */
	private static final int WANDER_INTERVAL = 120;
	private static final int HORIZONTAL_RANGE = 8;
	private static final int VERTICAL_RANGE = 4;

	private final VerityEntity verity;
	private final double speedModifier;
	private double targetX;
	private double targetY;
	private double targetZ;

	public ModeStrollGoal(VerityEntity verity, double speedModifier) {
		this.verity = verity;
		this.speedModifier = speedModifier;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		if (this.verity.getMode() != VerityMode.WANDER) {
			return false;
		}
		if (this.verity.getRandom().nextInt(WANDER_INTERVAL) != 0) {
			return false;
		}
		Vec3 pos = DefaultRandomPos.getPos(this.verity, HORIZONTAL_RANGE, VERTICAL_RANGE);
		if (pos == null) {
			return false;
		}
		this.targetX = pos.x;
		this.targetY = pos.y;
		this.targetZ = pos.z;
		return true;
	}

	@Override
	public boolean canContinueToUse() {
		return this.verity.getMode() == VerityMode.WANDER && !this.verity.getNavigation().isDone();
	}

	@Override
	public void start() {
		this.verity.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, this.speedModifier);
	}

	@Override
	public void stop() {
		this.verity.getNavigation().stop();
	}
}
