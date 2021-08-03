package bl4ckscor3.mod.theplopper.plopper.client;

import bl4ckscor3.mod.theplopper.ThePlopper;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(bus=Bus.MOD, modid=ThePlopper.MOD_ID, value=Dist.CLIENT)
public class ClientReg
{
	@SubscribeEvent
	public static void onFMLClientSetup(FMLClientSetupEvent event)
	{
		event.enqueueWork(() -> ScreenManager.registerFactory(ThePlopper.cTypePlopper, PlopperScreen::new));
	}
}
