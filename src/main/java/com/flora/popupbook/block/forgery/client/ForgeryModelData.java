package com.flora.popupbook.block.forgery.client;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public final class ForgeryModelData {

    public static final ModelProperty<BlockState> TARGET_STATE = new ModelProperty<>();

    private ForgeryModelData() {
    }
}