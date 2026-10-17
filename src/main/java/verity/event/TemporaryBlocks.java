package verity.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

/**
 * Places a block for a short time, then puts back what was there before.
 * Everything still pending is restored when the server stops, so nothing is left behind in the world.
 */
public final class TemporaryBlocks {
	private static final class Entry {
		final ServerLevel level;
		final BlockPos pos;
		final BlockState original;
		final Block placed;
		int ticksLeft;

		Entry(ServerLevel level, BlockPos pos, BlockState original, Block placed, int ticksLeft) {
			this.level = level;
			this.pos = pos;
			this.original = original;
			this.placed = placed;
			this.ticksLeft = ticksLeft;
		}

		void restore() {
			if (this.level.getBlockState(this.pos).is(this.placed)) {
				this.level.setBlockAndUpdate(this.pos, this.original);
			}
		}
	}

	private static final List<Entry> ENTRIES = new ArrayList<>();

	private TemporaryBlocks() {
	}

	public static void init() {
		ServerTickEvents.END_SERVER_TICK.register(server -> tick());
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> restoreAll());
	}

	public static void place(ServerLevel level, BlockPos pos, BlockState temporary, int ticks) {
		BlockState original = level.getBlockState(pos);
		level.setBlockAndUpdate(pos, temporary);
		ENTRIES.add(new Entry(level, pos.immutable(), original, temporary.getBlock(), ticks));
	}

	private static void tick() {
		Iterator<Entry> iterator = ENTRIES.iterator();
		while (iterator.hasNext()) {
			Entry entry = iterator.next();
			entry.ticksLeft--;
			if (entry.ticksLeft <= 0) {
				entry.restore();
				iterator.remove();
			}
		}
	}

	private static void restoreAll() {
		for (Entry entry : ENTRIES) {
			entry.restore();
		}
		ENTRIES.clear();
	}
}
