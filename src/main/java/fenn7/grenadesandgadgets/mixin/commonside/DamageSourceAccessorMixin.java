package fenn7.grenadesandgadgets.mixin.commonside;

import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DamageSource.class)
public interface DamageSourceAccessorMixin {
    @Invoker("<init>")
    static DamageSource grenadesandgadgets$invokeConstructor(String name) {
        throw new UnsupportedOperationException("If you are seeing this message, the moon will crash into the earth in 3 days.");
    }
}
