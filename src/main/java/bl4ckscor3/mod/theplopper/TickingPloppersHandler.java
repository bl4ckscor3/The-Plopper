package bl4ckscor3.mod.theplopper;

import java.util.ArrayList;
import java.util.List;

import bl4ckscor3.mod.theplopper.tileentity.TileEntityPlopper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

@EventBusSubscriber(modid=ThePlopper.MOD_ID)
public class TickingPloppersHandler
{
	private static final List<TileEntityPlopper> stopTickingClient = new ArrayList<>();
	private static final List<TileEntityPlopper> stopTickingServer = new ArrayList<>();

	/**
	 * Stops the given tile entity from being ticked further
	 * @param te The tile entity to stop ticking
	 */
	public static void stopTicking(TileEntityPlopper te)
	{
		if(te.getWorld().isRemote)
			stopTickingClient.add(te);
		else
			stopTickingServer.add(te);
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event)
	{
		for(TileEntityPlopper te : stopTickingClient)
		{
			te.getWorld().tickableTileEntities.remove(te);
		}

		stopTickingClient.clear();
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event)
	{
		for(TileEntityPlopper te : stopTickingServer)
		{
			te.getWorld().tickableTileEntities.remove(te);
		}

		stopTickingServer.clear();
	}
}
