package owo.cursed.intercrus_tech.blocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import owo.cursed.intercrus_tech.IntercrusTechMod;
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

}
