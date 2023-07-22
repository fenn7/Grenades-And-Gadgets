package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import static fenn7.grenadesandgadgets.client.network.GrenadesModS2CPackets.SYNC_GRENADE_S2C;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.CATACLYSMIC;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.ELASTIC;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.GRAVITY;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.LEVITY;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.MOLTEN;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.POTENT;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.REACTIVE;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.STICKY;

import java.util.List;

import fenn7.grenadesandgadgets.commonside.entity.grenades.AbstractGrenadeEntity;
import fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractGrenadeItem extends Item {
    private static final float MASS_BOUNCE_MULTIPLIER = 1.125F;
    private static final float MASS_VELOCITY_MULTIPLIER = 1.15F;
    private static final float MASS_POWER_MULTIPLIER = 1.1F;
    private static final float ELASTIC_BOUNCE_FACTOR = 0.95F;
    private static final float REACTIVE_AGE_MULTIPLIER = 0.8F;
    private static final float MOLTEN_BOUNCE_MULTIPLIER = 1.2F;
    private static final float POTENCY_MULTIPLIER = 1.2F;
    private static final float CATACLYSMIC_BOUNCE_MULTIPLIER = 0.9F;
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
        AbstractGrenadeEntity grenade = this.createGrenadeAt(world, user, user.getStackInHand(hand));
        grenade.setItem(stack);
        float speed = this.defaultSpeed;
        switch (stack.getOrCreateNbt().getString(GrenadeModifierRecipe.MODIFIER_KEY)) {
            case STICKY -> grenade.setShouldBounce(false);
            case ELASTIC -> grenade.setBounceMultiplier(ELASTIC_BOUNCE_FACTOR);
            case POTENT -> grenade.setPower(grenade.getPower() * POTENCY_MULTIPLIER);
            case REACTIVE -> {
                grenade.setShouldBounce(false);
                grenade.setMaxAgeTicks(Math.round(grenade.getMaxAgeTicks() * REACTIVE_AGE_MULTIPLIER));
            }
            case MOLTEN -> grenade.setBounceMultiplier(grenade.getBounceMultiplier() * MOLTEN_BOUNCE_MULTIPLIER);
            case LEVITY -> {
                speed *= MASS_VELOCITY_MULTIPLIER;
                grenade.setBounceMultiplier(grenade.getBounceMultiplier() * MASS_BOUNCE_MULTIPLIER);
                grenade.setPower(grenade.getPower() / MASS_POWER_MULTIPLIER);
            }
            case GRAVITY -> {
                speed /= MASS_VELOCITY_MULTIPLIER;
                grenade.setBounceMultiplier(grenade.getBounceMultiplier() / MASS_BOUNCE_MULTIPLIER);
                grenade.setPower(grenade.getPower() * MASS_POWER_MULTIPLIER);
            }
            case CATACLYSMIC -> grenade.setBounceMultiplier(grenade.getBounceMultiplier() * CATACLYSMIC_BOUNCE_MULTIPLIER);
        }
        this.setPitchYawVelocity(user, grenade, this.defaultRoll, speed, this.defaultDiv);
        if (!world.isClient()) {
            world.spawnEntity(grenade);
            ServerPlayNetworking.send((ServerPlayerEntity) user, SYNC_GRENADE_S2C, this.buildGrenadeBuf(grenade));
        }
        if (!user.isCreative()) {
            stack.decrement(1);
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    private PacketByteBuf buildGrenadeBuf(AbstractGrenadeEntity grenade) {
        var buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(grenade.getId());
        buf.writeInt(grenade.getMaxAgeTicks());
        buf.writeBoolean(grenade.getShouldBounce());
        buf.writeFloat(grenade.getBounceMultiplier());
        buf.writeFloat(grenade.getPower());
        return buf;
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
