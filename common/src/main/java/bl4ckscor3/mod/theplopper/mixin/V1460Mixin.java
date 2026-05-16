package bl4ckscor3.mod.theplopper.mixin;

import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;

import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.V1460;

/**
 * Registers the plopper to datafixers, so the item stacks in it can be fixed.
 */
@Mixin(V1460.class)
public class V1460Mixin {
	@Inject(method = "registerBlockEntities", at = @At("TAIL"))
	private void theplopper$registerBlockEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> ci, @Local Map<String, Supplier<TypeTemplate>> map) {
		//@formatter:off
		schema.register(map, "theplopper:plopper", () -> DSL.optionalFields("PlopperInventory", DSL.optionalFields(
				Pair.of("Slot0", References.ITEM_STACK.in(schema)),
				Pair.of("Slot1", References.ITEM_STACK.in(schema)),
				Pair.of("Slot2", References.ITEM_STACK.in(schema)),
				Pair.of("Slot3", References.ITEM_STACK.in(schema)),
				Pair.of("Slot4", References.ITEM_STACK.in(schema)),
				Pair.of("Slot5", References.ITEM_STACK.in(schema)),
				Pair.of("Slot6", References.ITEM_STACK.in(schema)),
				Pair.of("Slot7", References.ITEM_STACK.in(schema)))));
	}
}
