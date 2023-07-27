package fenn7.grenadesandgadgets.mixin.commonside;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {
    @Redirect(method = "getCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;raycast(Lnet/minecraft/world/RaycastContext;)Lnet/minecraft/util/hit/BlockHitResult;"))
    private static BlockHitResult grenadesandGadgets$redirectMoltenHitDetection(World instance, RaycastContext raycastContext, Entity entity) {
        if (entity instanceof AbstractGrenadeEntity grenade && grenade.getModifierName().equals(GrenadeModifierRecipe.MOLTEN)) {
            Vec3d vec3d = entity.getVelocity();
            World world = entity.world;
            Vec3d vec3d2 = entity.getPos();
            Vec3d vec3d3 = vec3d2.add(vec3d);
            var hitResult = world.raycast(new RaycastContext(vec3d2, vec3d3, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.WATER, entity));
            if (hitResult.getType() != BlockHitResult.Type.MISS && world.getBlockState(hitResult.getBlockPos()).isOf(Blocks.WATER)) {
                BlockPos pos = hitResult.getBlockPos();
                switch (hitResult.getSide().asString()) {
                    case "up", "down" -> world.setBlockState(pos, Blocks.STONE.getDefaultState());
                    default -> world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
                }
                world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.HOSTILE, 0.75F, 1.0F);
            }
            return hitResult;
        }
        return instance.raycast(raycastContext);
    }
}
