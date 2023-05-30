package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.FireGrenadeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class FireGrenadeItem extends AbstractGrenadeItem {
    public FireGrenadeItem(Settings settings) {
        super(settings);
        this.defaultSpeed = 0.7F;
    }

    @Override
    public AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, Hand hand) {
        return new FireGrenadeEntity(world, player);
    }
}
