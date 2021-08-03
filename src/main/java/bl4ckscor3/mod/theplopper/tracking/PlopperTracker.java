package bl4ckscor3.mod.theplopper.tracking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bl4ckscor3.mod.theplopper.block.PlopperTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Tracks all exisiting ploppers so searching for them each time an item expires is obsolete.
 * Also manages range checks
 */
public class PlopperTracker
{
	private static final Map<ResourceKey<Level>,Collection<BlockPos>> trackedPloppers = new HashMap<>();

	/**
	 * Starts tracking a plopper
	 * @param te The plopper to track
	 */
	public static void track(PlopperTileEntity te)
	{
		getTrackedPloppers(te.getLevel()).add(te.getBlockPos().immutable());
	}

	/**
	 * Stops tracking the given plopper. Use when e.g. removing the tile entity from the world
	 * @param te The plopper to stop tracking
	 */
	public static void stopTracking(PlopperTileEntity te)
	{
		getTrackedPloppers(te.getLevel()).remove(te.getBlockPos());
	}

	/**
	 * Gets all ploppers that have the given block position in their range in the given world
	 * @param world The world
	 * @param pos The block position
	 * @return A list of all ploppers that have the given block position in their range
	 */
	public static List<PlopperTileEntity> getPloppersInRange(Level world, BlockPos pos)
	{
		final Collection<BlockPos> ploppers = getTrackedPloppers(world);
		List<PlopperTileEntity> returnValue = new ArrayList<>();

		for(Iterator<BlockPos> it = ploppers.iterator(); it.hasNext(); )
		{
			BlockPos plopperPos = it.next();

			if(plopperPos != null)
			{
				BlockEntity potentialPlopper = world.getBlockEntity(plopperPos);

				if(potentialPlopper instanceof PlopperTileEntity)
				{
					if(canPlopperReach((PlopperTileEntity)potentialPlopper, pos))
						returnValue.add((PlopperTileEntity)potentialPlopper);

					continue;
				}
			}

			it.remove();
		}

		return returnValue;
	}

	/**
	 * Gets all block positions at which a plopper is being tracked for the given world
	 * @param world The world to get the tracked ploppers of
	 */
	private static Collection<BlockPos> getTrackedPloppers(Level world)
	{
		Collection<BlockPos> ploppers = trackedPloppers.get(world.dimension());

		if(ploppers == null)
		{
			ploppers = new HashSet<>();
			trackedPloppers.put(world.dimension(), ploppers);
		}

		return ploppers;
	}

	/**
	 * Checks whether the given block position is contained in the given plopper's range
	 * @param te The plopper
	 * @param pos The block position
	 */
	private static boolean canPlopperReach(PlopperTileEntity te, BlockPos pos)
	{
		AABB plopperRange = te.getRange();

		return plopperRange.minX <= pos.getX() && plopperRange.minY <= pos.getY() && plopperRange.minZ <= pos.getZ() && plopperRange.maxX >= pos.getX() && plopperRange.maxY >= pos.getY() && plopperRange.maxZ >= pos.getZ();
	}
}
