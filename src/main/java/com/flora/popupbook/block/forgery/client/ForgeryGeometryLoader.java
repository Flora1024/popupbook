package com.flora.popupbook.block.forgery.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class ForgeryGeometryLoader implements IGeometryLoader<ForgeryGeometry> {

    public static final ForgeryGeometryLoader INSTANCE = new ForgeryGeometryLoader();

    private ForgeryGeometryLoader() {
    }

    @Override
    public ForgeryGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        BlockModel defaultModel = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "default"), BlockModel.class);
        return new ForgeryGeometry(defaultModel);
    }
}