package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.SmokeFlareGrenadeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SmokeFlareGrenadeItem extends AbstractGrenadeItem {
    public SmokeFlareGrenadeItem(Settings settings) {
        super(settings);
    }

    @Override
    public AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, ItemStack stack) {
        return new SmokeFlareGrenadeEntity(world, player);
    }
}
