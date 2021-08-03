package bl4ckscor3.mod.theplopper.tracking;

import java.util.ArrayList;
import java.util.List;

import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.block.PlopperTileEntity;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid=ThePlopper.MOD_ID)
public class TickingPloppersHandler
{
	private static final List<PlopperTileEntity> stopTickingClient = new ArrayList<>();
	private static final List<PlopperTileEntity> stopTickingServer = new ArrayList<>();

	/**
	 * Stops the given tile entity from being ticked further
	 * @param te The tile entity to stop ticking
	 */
	public static void stopTicking(PlopperTileEntity te)
	{
		if(te.getLevel().isClientSide)
			stopTickingClient.add(te);
		else
			stopTickingServer.add(te);
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event)
	{
		for(PlopperTileEntity te : stopTickingClient)
		{
			te.getLevel().tickableBlockEntities.remove(te);
		}

		stopTickingClient.clear();
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event)
	{
		for(PlopperTileEntity te : stopTickingServer)
		{
			te.getLevel().tickableBlockEntities.remove(te);
		}

		stopTickingServer.clear();
	}
}
