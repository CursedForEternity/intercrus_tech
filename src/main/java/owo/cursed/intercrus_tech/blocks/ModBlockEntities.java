package owo.cursed.intercrus_tech.blocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import owo.cursed.intercrus_tech.IntercrusTechMod;
import owo.cursed.intercrus_tech.blocks.autocannon.AutocannonBlockEntity;
import owo.cursed.intercrus_tech.blocks.cannon.CannonBarrelEntity;
import owo.cursed.intercrus_tech.blocks.cannon.CannonLoaderEntity;
import owo.cursed.intercrus_tech.blocks.solar_impulse_drive.SolarImpulseCoolerEntity;
import owo.cursed.intercrus_tech.blocks.solar_impulse_drive.SolarImpulseDriveEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, IntercrusTechMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<SolarImpulseDriveEntity>> SOLAR_IMPULSE_DRIVE_ENTITY =
            BLOCK_ENTITIES.register("solar_impulse_drive_entity",
                    () -> BlockEntityType.Builder.of(SolarImpulseDriveEntity::new, ModBlocks.SOLAR_IMPULSE_DRIVE.get()).build(null));

    public static final RegistryObject<BlockEntityType<SolarImpulseCoolerEntity>> SOLAR_IMPULSE_COOLER_ENTITY =
            BLOCK_ENTITIES.register("solar_impulse_cooler_entity",
                    () -> BlockEntityType.Builder.of(SolarImpulseCoolerEntity::new, ModBlocks.SOLAR_IMPULSE_COOLER.get()).build(null));


    public static final RegistryObject<BlockEntityType<AutocannonBlockEntity>> AUTOCANNON_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("autocannon_entity",
                    () -> BlockEntityType.Builder.of(AutocannonBlockEntity::new, ModBlocks.AUTOCANNON.get()).build(null));

    public static final RegistryObject<BlockEntityType<CannonBarrelEntity>> CANNON_BARREL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("cannon_barrel_entity",
                    () -> BlockEntityType.Builder.of(CannonBarrelEntity::new, ModBlocks.CANNON_BARREL.get()).build(null));

    public static final RegistryObject<BlockEntityType<CannonLoaderEntity>> CANNON_LOADER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("cannon_loader_entity",
                    () -> BlockEntityType.Builder.of(CannonLoaderEntity::new, ModBlocks.CANNON_LOADER.get()).build(null));


}
