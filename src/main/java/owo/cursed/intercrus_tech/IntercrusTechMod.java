package owo.cursed.intercrus_tech;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.api.ValkyrienSkies;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import owo.cursed.intercrus_tech.blocks.ModBlockEntities;
import owo.cursed.intercrus_tech.blocks.ModBlocks;
import owo.cursed.intercrus_tech.items.ModCreativeTabs;
import owo.cursed.intercrus_tech.items.ModItems;
import owo.cursed.intercrus_tech.ship.IntercrusShipAttachment;

import java.util.Optional;

@Mod(IntercrusTechMod.MOD_ID)
public class IntercrusTechMod {
    public static final String MOD_ID = "intercrus_tech";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public IntercrusTechMod(FMLJavaModLoadingContext context) {
        IEventBus eventBus = context.getModEventBus();

        ModBlocks.BLOCKS.register(eventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(eventBus);
        ModItems.ITEMS.register(eventBus);
        ModCreativeTabs.register(eventBus);

        ValkyrienSkies.api().registerAttachment(IntercrusShipAttachment.class);

    }

    public static ResourceLocation asResource(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }

    public static Optional<LoadedServerShip> getShipAt(ServerLevel serverLevel, BlockPos pos) {

        ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, pos);
        if (ship == null){
            ship = VSGameUtilsKt.getShipManagingPos(serverLevel, pos);
        }
        if (ship instanceof LoadedServerShip loadedServerShip) {
            return Optional.of(loadedServerShip);
        }
        return Optional.empty();
    }
}
