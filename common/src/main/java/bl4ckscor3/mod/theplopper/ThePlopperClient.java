package bl4ckscor3.mod.theplopper;

import bl4ckscor3.mod.theplopper.client.PlopperScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ThePlopperClient {
	private ThePlopperClient() {}

	public static void registerMenuScreen(MenuScreenRegistration event) {
		event.register(ThePlopper.PLOPPER_MENU_TYPE.get(), PlopperScreen::new);
	}

	@FunctionalInterface
	public interface MenuScreenRegistration {
		<M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void register(MenuType<M> menuType, ScreenConstructor<M, S> screenConstructor);
	}

	@FunctionalInterface
	public interface ScreenConstructor<M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> {
		S create(M menu, Inventory inv, Component title);
	}
}
