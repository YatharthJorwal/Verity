package verity.client;

import net.minecraft.client.renderer.entity.EntityRenderers;

import net.fabricmc.api.ClientModInitializer;

import verity.ModEntities;

public class VerityClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		VerityModelLayers.register();
		EntityRenderers.register(ModEntities.VERITY, VerityRenderer::new);
	}
}
