package owo.cursed.intercrus_tech.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import owo.cursed.intercrus_tech.IntercrusTechMod;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IntercrusTechMod.MOD_ID);
    public static final RegistryObject<CreativeModeTab> INTERCRUS_TAB = CREATIVE_MODE_TABS.register("intercrus_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.literal("Intercrus Technologies INC"))
                    .icon(() -> new ItemStack(ModItems.SOLAR_IMPULSE_COOLER_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        // Add all our items to the tab
                        ModItems.ITEMS.getEntries().forEach(itemRegistryObject -> {
                                output.accept(itemRegistryObject.get());
                        });
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
