package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import fenn7.grenadesandgadgets.commonside.entity.misc.DecoyEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class DecoyGrenadeItem extends Item {
    public DecoyGrenadeItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        DecoyEntity decoyEntity = new DecoyEntity(world, user, 5.0F);
        decoyEntity.setPosition(user.getPos());
        world.spawnEntity(decoyEntity);
        return super.use(world, user, hand);
    }
}
