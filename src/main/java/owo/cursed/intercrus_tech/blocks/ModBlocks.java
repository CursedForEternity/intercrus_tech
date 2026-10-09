package owo.cursed.intercrus_tech.blocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import owo.cursed.intercrus_tech.IntercrusTechMod;
import owo.cursed.intercrus_tech.blocks.solar_impulse_drive.SolarImpulseCooler;
import owo.cursed.intercrus_tech.blocks.solar_impulse_drive.SolarImpulseDrive;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, IntercrusTechMod.MOD_ID);


    public static final RegistryObject<Block> SOLAR_IMPULSE_COOLER = BLOCKS.register("solar_impulse_cooler",
            () -> new SolarImpulseCooler(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)
            )
    );

    public static final RegistryObject<Block> SOLAR_IMPULSE_DRIVE = BLOCKS.register("solar_impulse_drive",
            () -> new SolarImpulseDrive(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK)
            )
    );

}
