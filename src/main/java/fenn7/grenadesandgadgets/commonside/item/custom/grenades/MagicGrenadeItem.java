package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.MagicGrenadeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MagicGrenadeItem extends AbstractGrenadeItem {
    public MagicGrenadeItem(Settings settings) {
        super(settings);
        this.defaultSpeed = 0.65F;
    }

    @Override
    protected AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, ItemStack stack) {
        return new MagicGrenadeEntity(world, player);
    }
}
