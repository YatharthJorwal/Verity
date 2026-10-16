package verity.entity.goal;

import net.minecraft.world.entity.ai.goal.RandomStrollGoal;

import verity.entity.VerityEntity;
import verity.entity.VerityMode;

/** Vanilla random wandering, but only while Verity is in WANDER mode. */
public class ModeStrollGoal extends RandomStrollGoal {
	private final VerityEntity verity;

	public ModeStrollGoal(VerityEntity verity, double speedModifier) {
		super(verity, speedModifier);
		this.verity = verity;
	}

	@Override
	public boolean canUse() {
		return this.verity.getMode() == VerityMode.WANDER && super.canUse();
	}

	@Override
	public boolean canContinueToUse() {
		return this.verity.getMode() == VerityMode.WANDER && super.canContinueToUse();
	}
}
