package bl4ckscor3.mod.theplopper;

import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public class FabricClientEntrypoint implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ConfigScreenFactoryRegistry.INSTANCE.register(ThePlopper.MODID, ConfigurationScreen::new);
		ThePlopperClient.registerMenuScreen(new ThePlopperClient.MenuScreenRegistration() {
			@Override
			public <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void register(MenuType<M> menuType, ThePlopperClient.ScreenConstructor<M, S> screenConstructor) {
				MenuScreens.register(menuType, screenConstructor::create);
			}
		});
	}
}
