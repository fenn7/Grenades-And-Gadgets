package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class IceGrenadeItem extends Item {
    public IceGrenadeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        StatusEffectInstance s = new StatusEffectInstance(GrenadesModStatus.FROZEN, 100, 0);
        entity.addStatusEffect(s);
        if (!entity.world.isClient) {
            try {
                entity.world.sendPacket(new EntityStatusEffectS2CPacket(entity.getId(), s));
            } catch (UnsupportedOperationException ignored) {}
        }
        return super.useOnEntity(stack, user, entity, hand);
    }
}
