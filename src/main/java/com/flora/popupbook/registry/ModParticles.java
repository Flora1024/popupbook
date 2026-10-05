package com.flora.popupbook.registry;

import com.flora.popupbook.PopupBook;
import com.flora.popupbook.particle.MagicSphereTrailParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, PopupBook.MODID);

    public static final Supplier<SimpleParticleType> MAGIC_SPHERE_TRAIL_PARTICLES = PARTICLES.register("magic_sphere_trail_particles",
            () -> new SimpleParticleType(true));

}
