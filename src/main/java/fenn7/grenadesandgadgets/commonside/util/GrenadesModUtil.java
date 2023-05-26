package fenn7.grenadesandgadgets.commonside.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface GrenadesModUtil {
    static Text textOf(String text) {
        return Text.of(text);
    }

    static void createExplosionEffects(World world, ParticleEffect effect, Vec3d location, int number,  double power) {
        if (world.isClient) {
            for (int i = 0; i < number; ++i) {
                double rand1 = ThreadLocalRandom.current().nextDouble(-power, power);
                double rand2 = ThreadLocalRandom.current().nextDouble(-power, power);
                world.addParticle(effect, location.x, location.y, location.z, power + rand1, 2 * power, power + rand2);
            }
        }
    }

    static void playExplosionSound(World world, GrenadesModSoundProfile soundProfile, Vec3d location) {
        world.playSound(location.x, location.y, location.z, soundProfile.soundEvent(), SoundCategory.HOSTILE, soundProfile.volume(), soundProfile.pitch(), true);
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
