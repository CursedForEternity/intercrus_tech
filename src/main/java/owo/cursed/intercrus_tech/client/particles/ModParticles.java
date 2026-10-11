package owo.cursed.intercrus_tech.client.particles;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import owo.cursed.intercrus_tech.IntercrusTechMod;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, IntercrusTechMod.MOD_ID);

    public static final RegistryObject<SimpleParticleType> TRACER_SMALL =
            PARTICLE_TYPES.register("tracer_small", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> TRACER_BIG =
            PARTICLE_TYPES.register("tracer_big", () -> new SimpleParticleType(true));
}