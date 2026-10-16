package verity.client;

import net.minecraft.client.model.geom.ModelLayerLocation;

import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;

import verity.Verity;

public class VerityModelLayers {
	public static final ModelLayerLocation VERITY = new ModelLayerLocation(Verity.id("verity"), "main");

	public static void register() {
		ModelLayerRegistry.registerModelLayer(VERITY, VerityModel::createLayer);
	}
}
