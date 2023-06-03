package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.RadiantGrenadeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RadiantGrenadeItem extends AbstractGrenadeItem {
    public RadiantGrenadeItem(Settings settings) {
        super(settings);
        this.defaultSpeed = 0.7F;
    }

    @Override
    public AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, ItemStack stack) {
        return new RadiantGrenadeEntity(world, player);
    }
}
