package owo.cursed.intercrus_tech.blocks.cannon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import owo.cursed.intercrus_tech.blocks.ModBlockEntities;

public class CannonLoaderEntity extends BlockEntity {
    private static final int MAX_AMMO_CAPACITY = 8;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return MAX_AMMO_CAPACITY;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(Items.IRON_BLOCK); // Change to Items.ARROW if using arrows
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final LazyOptional<ItemStackHandler> handlerLazyOptional = LazyOptional.of(() -> itemHandler);

    public CannonLoaderEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CANNON_LOADER_BLOCK_ENTITY.get(), pos, blockState);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public boolean isPowered() {
        if (level == null) return false;
        return level.hasNeighborSignal(worldPosition) || level.getBestNeighborSignal(worldPosition) > 0;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return handlerLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handlerLazyOptional.invalidate();
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
    }
}