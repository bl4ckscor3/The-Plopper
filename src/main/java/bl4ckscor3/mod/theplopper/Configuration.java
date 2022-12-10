package bl4ckscor3.mod.theplopper;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class Configuration {
	public static final ForgeConfigSpec CONFIG_SPEC;
	public static final Configuration CONFIG;
	public final BooleanValue displayParticles;
	public final BooleanValue playSound;
	public final BooleanValue bypassOutputSide;

	static {
		Pair<Configuration, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Configuration::new);

		CONFIG_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	Configuration(ForgeConfigSpec.Builder builder) {
		//@formatter:off
		displayParticles = builder
				.comment("Whether particles should show up every time the plopper picks up an item")
				.define("displayParticles", true);
		playSound = builder
				.comment("Whether a sound should be played when an item gets picked up by the plopper")
				.define("playSound", true);
		bypassOutputSide = builder
				.comment("If set to true, the plopper will output items to e.g. pipes at all sides instead of just the bottom")
				.define("bypassOutputSide", false);
		//@formatter:on
	}
}
