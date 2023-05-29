package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.SmokeFlareGrenadeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SmokeFlareGrenadeItem extends AbstractGrenadeItem {
    public SmokeFlareGrenadeItem(Settings settings) {
        super(settings);
    }

    @Override
    protected AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, Hand hand) {
        return new SmokeFlareGrenadeEntity(world, player);
    }
}
