package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.GrenadeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class GrenadeItem extends AbstractGrenadeItem {
    public GrenadeItem(Settings settings) {
        super(settings);
    }

    @Override
    public AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, Hand hand) {
        return new GrenadeEntity(world, player);
    }
}
