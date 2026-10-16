package verity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

import verity.Verity;
import verity.entity.VerityEntity;

public class VerityRenderer extends MobRenderer<VerityEntity, LivingEntityRenderState, VerityModel> {
	private static final Identifier TEXTURE = Verity.id("textures/entity/verity.png");

	public VerityRenderer(EntityRendererProvider.Context context) {
		super(context, new VerityModel(context.bakeLayer(VerityModelLayers.VERITY)), 0.25f);
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		return TEXTURE;
	}
}
