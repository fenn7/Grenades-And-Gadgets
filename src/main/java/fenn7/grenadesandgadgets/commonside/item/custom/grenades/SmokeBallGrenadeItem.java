package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import fenn7.grenadesandgadgets.commonside.entity.projectiles.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.projectiles.SmokeBallGrenadeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class SmokeBallGrenadeItem extends AbstractGrenadeItem {
    public SmokeBallGrenadeItem(Settings settings) {
        super(settings);
        this.defaultSpeed = 0.8F;
    }

    @Override
    protected AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player) {
        return new SmokeBallGrenadeEntity(world, player);
    }
}
