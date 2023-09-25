package fenn7.grenadesandgadgets.commonside.item.custom.grenades;

import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.CATACLYSMIC;
import static fenn7.grenadesandgadgets.commonside.item.recipe.custom.GrenadeModifierRecipe.ECHOING;
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
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractGrenadeItem extends Item {
    public static final int DEFAULT_MAX_AGE = 100;
    public static final float DEFAULT_BOUNCE_MULTIPLIER = 2F / 3F;
    private static final float MASS_BOUNCE_MULTIPLIER = 1.125F;
    private static final float MASS_VELOCITY_MULTIPLIER = 1.15F;
    private static final float MASS_POWER_MULTIPLIER = 1.1F;
    private static final float ELASTIC_BOUNCE_FACTOR = 0.95F;
    private static final float REACTIVE_AGE_MULTIPLIER = 0.8F;
    private static final float MOLTEN_BOUNCE_MULTIPLIER = 1.2F;
    private static final float POTENCY_MULTIPLIER = 1.2F;
    private static final float CATACLYSMIC_BOUNCE_MULTIPLIER = 1.1F;
    private static final float CATACLYSMIC_AGE_MULTIPLIER = 0.7F;
    private static final float ECHOING_AGE_MULTIPLIER = 0.75F;
    protected float defaultRoll = 0.1F;
    protected float defaultSpeed = 0.75F;
    protected float defaultDiv = 0.2F;

    public AbstractGrenadeItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return Math.round(DEFAULT_MAX_AGE * switch (stack.getOrCreateNbt().getString(GrenadeModifierRecipe.MODIFIER_KEY)) {
            case CATACLYSMIC -> CATACLYSMIC_AGE_MULTIPLIER;
            case REACTIVE -> REACTIVE_AGE_MULTIPLIER;
            case ECHOING -> ECHOING_AGE_MULTIPLIER;
            default -> 1.0F;
        });
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            var grenade = this.spawnGrenadeRemainingTicks(stack, world, player, 0);
            grenade.setVelocity(Vec3d.ZERO);
        }
        return super.finishUsing(stack, world, user);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player) {
            this.spawnGrenadeRemainingTicks(stack, world, player, remainingUseTicks);
        }
    }

    private AbstractGrenadeEntity spawnGrenadeRemainingTicks(ItemStack stack, World world, PlayerEntity player, int remainingUseTicks) {
        player.getItemCooldownManager().set(this, 20);
        AbstractGrenadeEntity grenade = this.createGrenadeAt(world, player, stack);
        grenade.setItem(stack);
        grenade.setMaxAgeTicks(remainingUseTicks);
        float speed = this.defaultSpeed * addNbtModifier(stack, grenade);
        this.setPitchYawVelocity(player, grenade, this.defaultRoll, speed, this.defaultDiv);
        if (!player.isCreative() && !player.isSpectator()) {
            stack.decrement(1);
        }
        if (!world.isClient()) {
            world.spawnEntity(grenade);
        }
        return grenade;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    public static float addNbtModifier(ItemStack stack, AbstractGrenadeEntity grenade) {
        float speed = 1.0F;
        switch (stack.getOrCreateNbt().getString(GrenadeModifierRecipe.MODIFIER_KEY)) {
            case STICKY, ECHOING, REACTIVE -> grenade.setShouldBounce(false);
            case ELASTIC -> grenade.setBounceMultiplier(ELASTIC_BOUNCE_FACTOR);
            case POTENT -> grenade.setPower(grenade.getPower() * POTENCY_MULTIPLIER);
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
        return speed;
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

    public abstract AbstractGrenadeEntity createGrenadeAt(World world, PlayerEntity player, ItemStack stack);

    public float getDefaultSpeed() {
        return this.defaultSpeed;
    }
}

