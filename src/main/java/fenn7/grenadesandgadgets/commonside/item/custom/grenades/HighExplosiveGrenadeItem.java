package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.HighExplosiveGrenadeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class HighExplosiveGrenadeItem extends AbstractGrenadeItem {
    public HighExplosiveGrenadeItem(Settings settings) {
        super(settings);
        this.defaultSpeed = 0.6F;
    }

    @Override
    public AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, ItemStack stack) {
        return new HighExplosiveGrenadeEntity(world, player);
    }
}
