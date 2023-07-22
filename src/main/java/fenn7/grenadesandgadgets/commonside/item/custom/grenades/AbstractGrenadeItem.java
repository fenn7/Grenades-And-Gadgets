package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.GRAVITY;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.LEVITY;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.POTENT;

import java.util.List;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractGrenadeItem extends Item {
    protected float defaultRoll = 0.1F;
    protected float defaultSpeed = 0.75F;
    protected float defaultDiv = 0.2F;

    public AbstractGrenadeItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.getItemCooldownManager().set(this, 20);
        if (!world.isClient) {
            AbstractGrenadeEntity grenade = this.createGrenadeAt(world, user, user.getStackInHand(hand));
            grenade.setItem(stack);
            float speed = this.defaultSpeed;
            switch (stack.getOrCreateNbt().getString(GrenadeModifierRecipe.MODIFIER_KEY)) {
                case POTENT -> grenade.setPower(grenade.getPower() * 1.2F);
                case LEVITY -> speed *= 1.15F;
                case GRAVITY -> speed *= 0.85F;
            }
            this.setPitchYawVelocity(user, grenade, this.defaultRoll, speed, this.defaultDiv);
            world.spawnEntity(grenade);
        }
        if (!user.isCreative()) {
            stack.decrement(1);
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        String modifier = stack.getOrCreateNbt().getString(GrenadeModifierRecipe.MODIFIER_KEY);
        if (!modifier.isBlank()) {
            tooltip.add(GrenadesModUtil.textOf("§n§6" + modifier));
        }
    }

    protected void setPitchYawVelocity(PlayerEntity user, AbstractGrenadeEntity grenade, float roll, float speed, float divergence) {
        grenade.setVelocity(user, user.getPitch(), user.getYaw(), roll, speed, divergence);
    }

    protected abstract AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, ItemStack stack);

    public float getDefaultSpeed() {
        return this.defaultSpeed;
    }
}
