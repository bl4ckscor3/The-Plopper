package bl4ckscor3.mod.theplopper.plopper.client;

import bl4ckscor3.mod.theplopper.ThePlopper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(bus = Bus.MOD, modid = ThePlopper.MOD_ID, value = Dist.CLIENT)
public class ClientReg {
	@SubscribeEvent
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> MenuScreens.register(ThePlopper.PLOPPER_MENU_TYPE.get(), PlopperScreen::new));
	}
}
