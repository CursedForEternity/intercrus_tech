package owo.cursed.intercrus_tech.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import owo.cursed.intercrus_tech.IntercrusTechMod;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, IntercrusTechMod.MOD_ID);

    public static final RegistryObject<SoundEvent> CANNON_SHOT = registerSoundEvent("cannon");
    public static final RegistryObject<SoundEvent> AUTOCANNON_SHOT = registerSoundEvent("autocannon");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(IntercrusTechMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
