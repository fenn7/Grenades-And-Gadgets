package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import fenn7.grenadesandgadgets.commonside.entity.projectiles.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.FireGrenadeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class FireGrenadeItem extends AbstractGrenadeItem {
    public FireGrenadeItem(Settings settings) {
        super(settings);
        this.defaultSpeed = 0.675F;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return super.use(world, user, hand);
    }

    public AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player) {
        return new FireGrenadeEntity(world, player);
    }
}
