package verity.entity.goal;

import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal;

import verity.entity.VerityEntity;
import verity.entity.VerityMode;

/** Holds Verity in place while it is in STAY mode. It can still turn its head. */
public class StayGoal extends Goal {
	private final VerityEntity verity;

	public StayGoal(VerityEntity verity) {
		this.verity = verity;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		return this.verity.getMode() == VerityMode.STAY;
	}

	@Override
	public boolean canContinueToUse() {
		return this.verity.getMode() == VerityMode.STAY;
	}

	@Override
	public void start() {
		this.verity.getNavigation().stop();
	}
}
