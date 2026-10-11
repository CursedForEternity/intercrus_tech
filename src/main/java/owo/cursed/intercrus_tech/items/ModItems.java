package owo.cursed.intercrus_tech.items;

import g_mungus.zpl.item.tooltip.FERedstoneBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import owo.cursed.intercrus_tech.IntercrusTechMod;
import owo.cursed.intercrus_tech.blocks.ModBlocks;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, IntercrusTechMod.MOD_ID);

    public static final RegistryObject<Item> SOLAR_IMPULSE_COOLER_ITEM = ITEMS.register("solar_impulse_cooler",
            () -> new BlockItem(ModBlocks.SOLAR_IMPULSE_COOLER.get(), new Item.Properties()));

    public static final RegistryObject<Item> SOLAR_IMPULSE_DRIVE_ITEM = ITEMS.register("solar_impulse_drive",
            () -> new FERedstoneBlockItem(ModBlocks.SOLAR_IMPULSE_DRIVE.get(), new Item.Properties()));
    public static final RegistryObject<Item> AUTOCANNON_ITEM = ITEMS.register("autocannon",
            () -> new FERedstoneBlockItem(ModBlocks.AUTOCANNON.get(), new Item.Properties()));
    public static final RegistryObject<Item> CANNON_BARREL_ITEM = ITEMS.register("cannon_barrel",
            () -> new BlockItem(ModBlocks.CANNON_BARREL.get(), new Item.Properties()));
    public static final RegistryObject<Item> CANNON_LOADER_ITEM = ITEMS.register("cannon_loader",
            () -> new FERedstoneBlockItem(ModBlocks.CANNON_LOADER.get(), new Item.Properties()));


}
