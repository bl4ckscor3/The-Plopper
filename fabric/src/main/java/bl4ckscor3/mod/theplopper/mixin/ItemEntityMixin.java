package bl4ckscor3.mod.theplopper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import bl4ckscor3.mod.theplopper.ThePlopper;
import net.minecraft.world.entity.item.ItemEntity;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V", ordinal = 1))
	private void onItemDespawn(CallbackInfo ci) {
		ThePlopper.checkForPloppers((ItemEntity) (Object) this);
	}
}
