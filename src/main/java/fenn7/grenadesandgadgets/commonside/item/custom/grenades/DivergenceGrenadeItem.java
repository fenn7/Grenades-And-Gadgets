package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.ConvergenceGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.DivergenceGrenadeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DivergenceGrenadeItem extends AbstractGrenadeItem {
    public DivergenceGrenadeItem(Settings settings) {
        super(settings);
        this.defaultSpeed = 0.55F;
    }

    @Override
    protected AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, ItemStack stack) {
        return new DivergenceGrenadeEntity(world, player);
    }
}
