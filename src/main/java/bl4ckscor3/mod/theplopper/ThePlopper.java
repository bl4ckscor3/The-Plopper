package bl4ckscor3.mod.theplopper;

import java.util.Arrays;

import bl4ckscor3.mod.theplopper.block.BlockPlopper;
import bl4ckscor3.mod.theplopper.gui.GuiHandler;
import bl4ckscor3.mod.theplopper.tileentity.TileEntityPlopper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid=ThePlopper.MOD_ID, name=ThePlopper.NAME, version=ThePlopper.VERSION, acceptedMinecraftVersions="[" + ThePlopper.MC_VERSION + "]")
@EventBusSubscriber
public class ThePlopper
{
	public static final String MOD_ID = "theplopper";
	public static final String NAME = "The Plopper";
	public static final String VERSION = "v1.2";
	public static final String MC_VERSION = "1.12";
	public static Block thePlopper;
	public static Item rangeUpgrade;
	@Instance(MOD_ID)
	public ThePlopper instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ModMetadata meta = event.getModMetadata();

		meta.authorList = Arrays.asList(new String[]{"bl4ckscor3"});
		meta.autogenerated = false;
		meta.description = "Adds a hopper-like block that sucks up items which are about to despawn.";
		meta.modId = MOD_ID;
		meta.name = NAME;
		meta.version = VERSION;

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
	}

	@SubscribeEvent
	public static void onRegistryEventRegisterBlock(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(thePlopper = new BlockPlopper());
		GameRegistry.registerTileEntity(TileEntityPlopper.class, thePlopper.getRegistryName());
	}

	@SubscribeEvent
	public static void onRegistryEventRegisterItem(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(rangeUpgrade = new Item().setRegistryName(new ResourceLocation(MOD_ID, "range_upgrade")).setTranslationKey("theplopper:range_upgrade").setMaxStackSize(7).setCreativeTab(CreativeTabs.REDSTONE));
		event.getRegistry().register(new ItemBlock(thePlopper).setRegistryName(thePlopper.getRegistryName().toString()));
	}

	@SubscribeEvent
	public static void onModelRegistry(ModelRegistryEvent event)
	{
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(thePlopper), 0, new ModelResourceLocation(thePlopper.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(rangeUpgrade, 0, new ModelResourceLocation(rangeUpgrade.getRegistryName(), "inventory"));
	}

	@SubscribeEvent
	public static void onItemExpire(ItemExpireEvent event)
	{
		checkForPloppers(event.getEntityItem());
	}

	/**
	 * Checks for any ploppers in increasing ranges and makes them suck up the item if applicable
	 * @param ei The item to potentially suck up
	 */
	private static void checkForPloppers(EntityItem ei)
	{
		if(ei.getEntityWorld().isRemote)
			return;

		Vec3d eiPos = new Vec3d(ei.getPosition().getX(), ei.getPosition().getY(), ei.getPosition().getZ());
		int maxRange = 16; //16 is a good range in my opinion. not too large, but can still cover a big enough area
		Iterable<BlockPos> box = BlockPos.getAllInBox(ei.getPosition().down(maxRange).west(maxRange).north(maxRange), ei.getPosition().up(maxRange).east(maxRange).south(maxRange));

		//find ploppers that are within the maximum range of the item
		for(BlockPos pos : box)
		{
			TileEntity te = ei.getEntityWorld().getTileEntity(pos);

			//check if a found plopper can pick up the item
			if(te != null && te instanceof TileEntityPlopper)
			{
				TileEntityPlopper plopper = (TileEntityPlopper)te;
				int distanceToItem = (int)Math.floor(new Vec3d(plopper.getPos().getX(), plopper.getPos().getY(), plopper.getPos().getZ()).distanceTo(eiPos));

				if(plopper.getRange() >= distanceToItem)
				{
					//if there are multiple ploppers that could potentially pick up the item, this one will take as much as it can and let the rest be handled by others
					if(plopper.suckUp(ei, ei.getItem()))
						return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent event)
	{
		if(event.getModID().equals(MOD_ID))
			ConfigManager.sync(MOD_ID, Type.INSTANCE);
	}

	/**
	 * For Testing purposes
	 */
	//TODO: Comment out on release
	//	@SubscribeEvent
	//	public static void onItemToss(ItemTossEvent event)
	//	{
	//		checkForPloppers(event.getEntityItem());
	//	}
}
