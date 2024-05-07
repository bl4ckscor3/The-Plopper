package bl4ckscor3.mod.theplopper.plopper.client;

import bl4ckscor3.mod.theplopper.ThePlopper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(bus = Bus.MOD, modid = ThePlopper.MOD_ID, value = Dist.CLIENT)
public class ClientReg {
	private ClientReg() {}

	@SubscribeEvent
	public static void onFMLClientSetup(RegisterMenuScreensEvent event) {
		event.register(ThePlopper.PLOPPER_MENU_TYPE.get(), PlopperScreen::new);
	}
}
