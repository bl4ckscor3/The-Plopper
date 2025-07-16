package bl4ckscor3.mod.theplopper;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ThePlopper.MODID, dist = Dist.CLIENT)
public class ThePlopperClient {
	public ThePlopperClient(ModContainer modContainer) {
		modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
	}
}
