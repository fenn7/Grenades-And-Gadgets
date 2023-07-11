package fenn7.grenadesandgadgets.mixin.commonside;

import fenn7.grenadesandgadgets.commonside.util.GrenadesModEntityData;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class GrenadesModEntityDataMixin implements GrenadesModEntityData {
    private static final Pair<String, Integer> MOD_DATA = new Pair<>("grenadesandgadgets.entity_data", 10);
    private NbtCompound persistentData;

    @Override
    public NbtCompound getPersistentData() {
        if(this.persistentData == null) {
            this.persistentData = new NbtCompound();
        }
        return persistentData;
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    protected void injectWriteMethod(NbtCompound nbt, CallbackInfoReturnable info) {
        if(persistentData != null) {
            nbt.put(MOD_DATA.getLeft(), persistentData);
        }
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    protected void injectReadMethod(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains(MOD_DATA.getLeft(), MOD_DATA.getRight())) {
            persistentData = nbt.getCompound(MOD_DATA.getLeft());
        }
    }
}
