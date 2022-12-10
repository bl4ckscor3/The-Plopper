package bl4ckscor3.mod.theplopper.tracking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bl4ckscor3.mod.theplopper.block.PlopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Tracks all exisiting ploppers so searching for them each time an item expires is obsolete. Also manages range checks
 */
public class PlopperTracker {
	private static final Map<ResourceKey<Level>, Collection<BlockPos>> TRACKED_PLOPPERS = new HashMap<>();

	/**
	 * Starts tracking a plopper
	 *
	 * @param be The plopper to track
	 */
	public static void track(PlopperBlockEntity be) {
		getTrackedPloppers(be.getLevel()).add(be.getBlockPos().immutable());
	}

	/**
	 * Stops tracking the given plopper. Use when e.g. removing the block entity from the world
	 *
	 * @param be The plopper to stop tracking
	 */
	public static void stopTracking(PlopperBlockEntity be) {
		getTrackedPloppers(be.getLevel()).remove(be.getBlockPos());
	}

	/**
	 * Gets all ploppers that have the given block position in their range in the given level
	 *
	 * @param level The level
	 * @param pos The block position
	 * @return A list of all ploppers that have the given block position in their range
	 */
	public static List<PlopperBlockEntity> getPloppersInRange(Level level, BlockPos pos) {
		final Collection<BlockPos> ploppers = getTrackedPloppers(level);
		List<PlopperBlockEntity> returnValue = new ArrayList<>();

		for (Iterator<BlockPos> it = ploppers.iterator(); it.hasNext();) {
			BlockPos plopperPos = it.next();

			if (plopperPos != null && level.getBlockEntity(plopperPos) instanceof PlopperBlockEntity plopper) {
				if (canPlopperReach(plopper, pos))
					returnValue.add(plopper);

				continue;
			}

			it.remove();
		}

		return returnValue;
	}

	/**
	 * Gets all block positions at which a plopper is being tracked for the given level
	 *
	 * @param level The level to get the tracked ploppers of
	 */
	private static Collection<BlockPos> getTrackedPloppers(Level level) {
		Collection<BlockPos> ploppers = TRACKED_PLOPPERS.get(level.dimension());

		if (ploppers == null) {
			ploppers = new HashSet<>();
			TRACKED_PLOPPERS.put(level.dimension(), ploppers);
		}

		return ploppers;
	}

	/**
	 * Checks whether the given block position is contained in the given plopper's range
	 *
	 * @param be The plopper
	 * @param pos The block position
	 */
	private static boolean canPlopperReach(PlopperBlockEntity be, BlockPos pos) {
		AABB plopperRange = be.getRange();

		return plopperRange.minX <= pos.getX() && plopperRange.minY <= pos.getY() && plopperRange.minZ <= pos.getZ() && plopperRange.maxX >= pos.getX() && plopperRange.maxY >= pos.getY() && plopperRange.maxZ >= pos.getZ();
	}
}
