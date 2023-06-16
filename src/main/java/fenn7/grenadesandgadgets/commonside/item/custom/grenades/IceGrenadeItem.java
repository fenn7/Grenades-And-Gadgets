package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.IceGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class IceGrenadeItem extends AbstractGrenadeItem {
    public IceGrenadeItem(Settings settings) {
        super(settings);
        this.defaultSpeed = 0.7F;
    }

    @Override
    protected AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, ItemStack stack) {
        return new IceGrenadeEntity(world, player);
    }
}
