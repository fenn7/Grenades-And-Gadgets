package fenn7.grenadesandgadgets.client;

import java.util.concurrent.ThreadLocalRandom;

import fenn7.grenadesandgadgets.commonside.util.GrenadesModSoundProfile;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public interface GrenadesModClientUtil {
    static ParticleEffect getDustParticleType(int hexColour, float size) {
        return new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(hexColour)), size);
    }

    static ParticleEffect getMaxSizeDustParticleType(int hexColour) {
        return new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(hexColour)), AbstractDustParticleEffect.MAX_SCALE);
    }

    static void createExplosionEffects(World world, ParticleEffect effect, Vec3d location, int number, double power) {
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

}
