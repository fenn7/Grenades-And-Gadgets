package fenn7.grenadesandgadgets.commonside.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public interface GrenadesModUtil {
    static Text textOf(String text) {
        return Text.of(text);
    }

    static MutableText mutableTextOf(String text) {
        return Text.of(text).copy();
    }

    private ParticleEffect getSmokeParticleType(int hexColour) {
        return new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(hexColour)), AbstractDustParticleEffect.MAX_SCALE);
    }

    static List<LivingEntity> getLivingEntitiesAtRangeFromEntity(World world, Entity entity, double radius) {
        return world.getNonSpectatingEntities(LivingEntity.class, getCubicBoxAroundEntity(entity, radius)).stream()
            .filter(e -> e.distanceTo(entity) <= radius)
            .toList();
    }

    static Box getCubicBoxAroundEntity(Entity entity, double radius) {
        return new Box(entity.getX() - radius, entity.getY() - radius, entity.getZ() - radius,
            entity.getX() + radius, entity.getY() + radius, entity.getZ() + radius);
    }

    static Box getCubicBoxAroundPos(BlockPos pos, double radius) {
        return new Box(pos).expand(radius, radius, radius);
    }

    static List<BlockPos> getBlocksInSphereAroundPos(BlockPos centre, double radius) {
        List<BlockPos> blocks = new LinkedList<>();
        BlockPos.stream(getCubicBoxAroundPos(centre, radius))
            .filter(pos -> pos.isWithinDistance(centre, radius))
            .forEach(pos -> blocks.add(pos.toImmutable()));
        return blocks;
    }
}
