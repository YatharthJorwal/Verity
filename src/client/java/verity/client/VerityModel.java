package verity.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/**
 * A single 8x8x8 cube that acts as Verity's whole body and head.
 * Texture is 32x16, with the face on the front region (pixels 8..16, 8..16).
 * The cube pivots around its center and follows the head's pitch and yaw,
 * so Verity visibly looks up, down and sideways at what it is watching.
 */
public class VerityModel extends EntityModel<LivingEntityRenderState> {
	private final ModelPart body;

	public VerityModel(ModelPart root) {
		super(root);
		this.body = root.getChild(PartNames.BODY);
	}

	public static LayerDefinition createLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild(
				PartNames.BODY,
				CubeListBuilder.create().texOffs(0, 0).addBox(-4, -4, -4, 8, 8, 8),
				PartPose.offset(0, 20, 0)
		);
		return LayerDefinition.create(mesh, 32, 16);
	}

	@Override
	public void setupAnim(LivingEntityRenderState state) {
		super.setupAnim(state);
		this.body.xRot = (float) Math.toRadians(state.xRot);
		this.body.yRot = (float) Math.toRadians(state.yRot);
	}
}
