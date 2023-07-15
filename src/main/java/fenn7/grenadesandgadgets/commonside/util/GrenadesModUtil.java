package fenn7.grenadesandgadgets.commonside.util;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import fenn7.grenadesandgadgets.client.network.GrenadesModS2CPackets;
import fenn7.grenadesandgadgets.commonside.status.GrenadesModStatus;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public interface GrenadesModUtil {
    static Text textOf(String text) {
        return Text.of(text);
    }

    static TranslatableText translatableTextOf(String key) {
        return new TranslatableText(key);
    }

    static MutableText mutableTextOf(String text) {
        return Text.of(text).copy();
    }

    static AnimationFactory getAnimationFactoryFor(IAnimatable animatable) {
        return new AnimationFactory(animatable);
    }

    static Set<LivingEntity> getLivingEntitiesAtRangeFromEntity(World world, Entity entity, double radius) {
        return world.getNonSpectatingEntities(LivingEntity.class, getCubicBoxAroundEntity(entity, radius)).stream()
            .filter(e -> e.squaredDistanceTo(entity.getX(), entity.getBodyY(0), entity.getZ()) <= Math.pow(radius, 2)
                || e.squaredDistanceTo(entity.getX(), entity.getBodyY(1), entity.getZ()) <= Math.pow(radius, 2))
            .collect(Collectors.toSet());
    }

    static Box getCubicBoxAroundEntity(Entity entity, double radius) {
        return new Box(entity.getX() - radius, entity.getY() - radius, entity.getZ() - radius,
            entity.getX() + radius, entity.getY() + radius, entity.getZ() + radius);
    }

    static Box getCubicBoxAroundPos(BlockPos pos, double radius) {
        return new Box(pos).expand(radius, radius, radius);
    }

    static Set<BlockPos> getBlocksInSphereAroundPos(BlockPos centre, double radius) {
        Set<BlockPos> blocks = new HashSet<>();
        BlockPos.stream(getCubicBoxAroundPos(centre, radius))
            .filter(pos -> pos.isWithinDistance(centre, radius))
            .forEach(pos -> blocks.add(pos.toImmutable()));
        return blocks;
    }

    static boolean areAnyBlocksBetween(World world, BlockPos start, BlockPos end) {
        return world.raycast(new BlockStateRaycastContext(Vec3d.ofCenter(start), Vec3d.ofCenter(end),
            state -> state.getMaterial().isSolid())).getType() == HitResult.Type.BLOCK;
    }

    static void addEffectServerAndClient(LivingEntity entity, StatusEffectInstance effect) {
        entity.addStatusEffect(effect);
        if (!entity.world.isClient) {
            try {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeIntList(IntList.of(entity.getId(), StatusEffect.getRawId(effect.getEffectType()),
                    effect.getDuration(), effect.getAmplifier()));
                ServerPlayerEntity player = (ServerPlayerEntity) entity.world.getPlayers().get(0);
                ServerPlayNetworking.send(player, GrenadesModS2CPackets.ADD_EFFECT_S2C, buf);
            } catch (UnsupportedOperationException ignored) {}
        }
    }

    static void removeEffectServerAndClient(LivingEntity entity, StatusEffect effect) {
        if (entity.hasStatusEffect(effect)) {
            if (!entity.world.isClient) {
                try {
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    buf.writeIntList(IntList.of(entity.getId(), StatusEffect.getRawId(effect)));
                    ServerPlayerEntity player = (ServerPlayerEntity) entity.world.getPlayers().get(0);
                    ServerPlayNetworking.send(player, GrenadesModS2CPackets.REMOVE_EFFECT_S2C, buf);
                } catch (UnsupportedOperationException ignored) {}
            }
            entity.removeStatusEffect(GrenadesModStatus.FROZEN);
        }
    }

    static Set<Block> loadBlocksFromTag(TagKey<Block> blockTag) {
        var blocks = new HashSet<Block>();
        Registry.BLOCK.iterateEntries(blockTag).forEach(blockRegistryEntry -> blocks.add(blockRegistryEntry.value()));
        return blocks;
    }
}
