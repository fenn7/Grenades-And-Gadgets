package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.entity.grenades.IceGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
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
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        // remove after testing
        GrenadesModUtil.addEffectServerAndClient(entity, new StatusEffectInstance(GrenadesModStatus.FROZEN,
            (int) 60,
            (int) 0)
        );
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    protected AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, ItemStack stack) {
        return new IceGrenadeEntity(world, player);
    }
}
