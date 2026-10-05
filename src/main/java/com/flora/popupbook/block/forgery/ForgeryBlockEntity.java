package com.flora.popupbook.block.forgery;

import com.flora.popupbook.block.forgery.client.ForgeryModelData;
import com.flora.popupbook.registry.ModBlockEntityTypes;
import com.flora.popupbook.registry.ModDataComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class ForgeryBlockEntity extends BlockEntity {

    public ForgeryBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.FORGERY.get(), pos, blockState);
    }

    public Block getAuthenticity() {
        return this.components().getOrDefault(ModDataComponentTypes.AUTHENTICITY.get(), Blocks.AIR);
    }

    public void setAuthenticity(Block authenticity) {
        DataComponentMap.Builder builder = DataComponentMap.builder();
        builder.addAll(this.components());
        builder.set(ModDataComponentTypes.AUTHENTICITY.get(), authenticity);
        this.setComponents(builder.build());
        setChanged();
        requestModelDataUpdate();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
        }
    }

    @Override
    public ModelData getModelData() {
        Block authenticity = getAuthenticity();
        if (authenticity == Blocks.AIR) {
            return ModelData.EMPTY;
        }
        return ModelData.of(ForgeryModelData.TARGET_STATE, authenticity.defaultBlockState());
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}