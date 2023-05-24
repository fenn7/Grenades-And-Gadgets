package fenn7.grenadesandgadgets.commonside.util;

import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
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

    static void playExplosionSound(World world, SoundEvent sound, Vec3d location, float volume, float pitch) {
        world.playSound(location.x, location.y, location.z, sound, SoundCategory.HOSTILE, volume, pitch, true);
    }
}
