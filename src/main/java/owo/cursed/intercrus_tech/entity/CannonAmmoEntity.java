package owo.cursed.intercrus_tech.entity;

import g_mungus.zpl.ZeroPointLabsMod;
import g_mungus.zpl.entity.FastProjectileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import owo.cursed.intercrus_tech.client.particles.ModParticles;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CannonAmmoEntity extends FastProjectileEntity {
    private static final EntityDataAccessor<Long> EXCLUDED_SHIP_ID =
            SynchedEntityData.defineId(CannonAmmoEntity.class, EntityDataSerializers.LONG);
    private static final long NO_SHIP_ID = -1L;

    private static final Map<BlockPos, Float> BLOCK_MINING_PROGRESS = new HashMap<>();

    private float miningPower = 4.0f;

    public CannonAmmoEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public CannonAmmoEntity(Level level, double x, double y, double z, Vec3 velocity) {
        super(ModEntities.AUTOCANNON_AMMO.get(), level); // Keep your entity registry reference as set up
        this.setPos(x, y, z);
        this.setDeltaMovement(velocity);
    }

    public void setExcludedShipId(@Nullable Long shipId) {
        this.entityData.set(EXCLUDED_SHIP_ID, shipId != null ? shipId : NO_SHIP_ID);
    }

    @Nullable
    public Long getExcludedShipId() {
        long shipId = this.entityData.get(EXCLUDED_SHIP_ID);
        return shipId == NO_SHIP_ID ? null : shipId;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EXCLUDED_SHIP_ID, NO_SHIP_ID);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            double scale = ZeroPointLabsMod.getDimensionScale(this.level());

            // Pass scale through velocity parameters (will be used for sizing)
            this.level().addParticle(
                    ModParticles.TRACER_BIG.get(),
                    this.xOld, this.yOld, this.zOld,
                    scale, 0, 0  // Pass scale in xSpeed parameter
            );
        }

        if (this.level().isClientSide()) {
            return;
        }

        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            this.onHitEntity(entityHit);
            return;
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            BlockPos hitPos = blockHit.getBlockPos();

            Long excludedShipId = this.getExcludedShipId();
            if (excludedShipId != null) {
                Ship ship = VSGameUtilsKt.getShipManagingPos(this.level(), hitPos);
                if (ship != null && ship.getId() == excludedShipId) {
                    Vec3 motion = this.getDeltaMovement();
                    this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);
                    if (this.tickCount > 160) {
                        this.discard();
                    }
                    return;
                }
            }

            BlockState state = this.level().getBlockState(hitPos);
            if (state.getDestroySpeed(this.level(), hitPos) >= 0) {
                float hardness = state.getDestroySpeed(this.level(), hitPos);
                float damageIncrement = miningPower / Math.max(hardness, 0.1f) / 5.0f;

                float currentProgress = BLOCK_MINING_PROGRESS.getOrDefault(hitPos, 0.0f) + damageIncrement;
                int stage = Math.min((int) (currentProgress * 10), 9);
                this.level().destroyBlockProgress(hitPos.hashCode(), hitPos, stage);

                if (currentProgress >= 1.0f) {
                    this.level().destroyBlock(hitPos, true, this);
                    BLOCK_MINING_PROGRESS.remove(hitPos);
                    this.level().destroyBlockProgress(hitPos.hashCode(), hitPos, -1);
                } else {
                    BLOCK_MINING_PROGRESS.put(hitPos, currentProgress);
                }
            }
            this.discard();
            return;
        }
        Vec3 motion = this.getDeltaMovement();
        this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);
        if (this.tickCount > 160) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide()) {
            result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 12.0F);
            this.discard();
        }
    }

    @Override
    protected boolean canHitEntity(net.minecraft.world.entity.Entity target) {
        return super.canHitEntity(target) && !target.equals(this.getOwner());
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 4096.0D;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ExcludedShipId")) {
            this.setExcludedShipId(tag.getLong("ExcludedShipId"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        Long shipId = this.getExcludedShipId();
        if (shipId != null) {
            tag.putLong("ExcludedShipId", shipId);
        }
    }
}
