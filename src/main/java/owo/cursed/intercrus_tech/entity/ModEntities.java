package owo.cursed.intercrus_tech.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import owo.cursed.intercrus_tech.IntercrusTechMod;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, IntercrusTechMod.MOD_ID);

    public static final RegistryObject<EntityType<AutocannonAmmoEntity>> AUTOCANNON_AMMO =
            ENTITY_TYPES.register("autocannon_ammo", () -> EntityType.Builder.<AutocannonAmmoEntity>of(
                            AutocannonAmmoEntity::new,
                            MobCategory.MISC
                    )
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("autocannon_ammo"));

    public static final RegistryObject<EntityType<CannonAmmoEntity>> CANNON_AMMO =
            ENTITY_TYPES.register("cannon_ammo", () -> EntityType.Builder.<CannonAmmoEntity>of(
                            CannonAmmoEntity::new,
                            MobCategory.MISC
                    )
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("cannon_ammo"));
}
