package bl4ckscor3.mod.theplopper;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeInt;

@Config(modid=ThePlopper.MOD_ID, category="general")
public class Configuration
{
	@Comment("The range in blocks at which the plopper will still pick up items")
	@RangeInt(min=1, max=20)
	public static byte range = 3;

	@Comment("Whether particles should show up every time the plopper picks up an item")
	public static boolean displayParticles = true;

	@Comment("Whether a sound should be played when an item gets picked up by the plopper")
	public static boolean playSound = true;

	@Comment("If set to true, the plopper will output items to e.g. pipes at all sides instead of just the bottom")
	public static boolean bypassOutputSide = false;
}
