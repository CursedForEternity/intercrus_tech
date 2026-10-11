package owo.cursed.intercrus_tech.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.BlockEntityVisualizer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizerRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import owo.cursed.intercrus_tech.IntercrusTechMod;
import owo.cursed.intercrus_tech.blocks.ModBlockEntities;
import owo.cursed.intercrus_tech.blocks.solar_impulse_drive.SolarImpulseCoolerEntity;
import owo.cursed.intercrus_tech.client.drive.DriveExhaustBlockEntityRenderer;
import owo.cursed.intercrus_tech.client.flywheel.DriveVisual;
import owo.cursed.intercrus_tech.client.particles.ModParticles;
import owo.cursed.intercrus_tech.client.particles.TracerSmall;
import owo.cursed.intercrus_tech.entity.AutocannonAmmoRenderer;
import owo.cursed.intercrus_tech.entity.CannonAmmoEntity;
import owo.cursed.intercrus_tech.entity.CannonAmmoRenderer;
import owo.cursed.intercrus_tech.entity.ModEntities;
import team.lodestar.lodestone.systems.rendering.shader.ShaderHolder;

import static team.lodestar.lodestone.registry.client.LodestoneShaderRegistry.registerShader;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = IntercrusTechMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IntercrusModClient {
    public static final ShaderHolder DRIVE_SHADER = new ShaderHolder(ResourceLocation.fromNamespaceAndPath(IntercrusTechMod.MOD_ID, "drive"), DefaultVertexFormat.POSITION_TEX);

    @SubscribeEvent
    public static void shaderRegistry(RegisterShadersEvent event) {
        registerShader(event, DRIVE_SHADER);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.SOLAR_IMPULSE_COOLER_ENTITY.get(), DriveExhaustBlockEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.AUTOCANNON_AMMO.get(), AutocannonAmmoRenderer::new);
        event.registerEntityRenderer(ModEntities.CANNON_AMMO.get(), CannonAmmoRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.TRACER_SMALL.get(), TracerSmall.Provider::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            VisualizerRegistry.setVisualizer(
                    ModBlockEntities.SOLAR_IMPULSE_COOLER_ENTITY.get(),
                    new BlockEntityVisualizer<>() {
                        @Override
                        public BlockEntityVisual<? super SolarImpulseCoolerEntity> createVisual(VisualizationContext ctx, SolarImpulseCoolerEntity blockEntity, float partialTick) {
                            return new DriveVisual(ctx, blockEntity, partialTick);
                        }

                        @Override
                        public boolean skipVanillaRender(SolarImpulseCoolerEntity blockEntity) {
                            return true;
                        }
                    }
            );
        });
    }

}
