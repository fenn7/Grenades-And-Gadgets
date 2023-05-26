package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import fenn7.grenadesandgadgets.commonside.entity.projectiles.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.SmokeBallGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.SmokeFlareGrenadeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class SmokeFlareGrenadeItem extends AbstractGrenadeItem {
    public SmokeFlareGrenadeItem(Settings settings) {
        super(settings);
        this.defaultSpeed = 0.85F;
    }

    @Override
    protected AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player) {
        return new SmokeFlareGrenadeEntity(world, player);
    }
}
