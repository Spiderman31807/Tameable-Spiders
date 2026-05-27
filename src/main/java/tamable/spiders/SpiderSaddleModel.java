package tamable.spiders.client.model;

import tamable.spiders.TamableSpider;
import tamable.spiders.Animations;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.HierarchicalModel;

@OnlyIn(Dist.CLIENT)
public class SpiderSaddleModel extends HierarchicalModel<Entity> {
	private final ModelPart root;
	private final ModelPart body;

	public SpiderSaddleModel(ModelPart root) {
		super();
		this.root = root;
		this.body = root.getChild("body1");
		this.body.skipDraw = true;
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
		if (entity instanceof TamableSpider spider) {
			this.body.setPos(0f, 15f, 9f);
			this.body.setRotation(0f, 0f, 0f);
			this.animate(spider.getSittingAnimation(), spider.isInSittingPose() ? Animations.Sit : Animations.Stand, ageInTicks, 1f);
		}
	}

	public static LayerDefinition createLayer(boolean hasArmorLayer, boolean isCave) {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition definition = mesh.getRoot();
		PartDefinition body = definition.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F), PartPose.offset(0.0F, 15.0F, 9.0F));
		PartDefinition saddle = body.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(24, 8).addBox(-6, -8, -3, 12, 16, 8, new CubeDeformation(hasArmorLayer ? 0 : -0.75f)), PartPose.offsetAndRotation(0, 0, 0, 1.5708f, 0, 0));
		return !isCave ? LayerDefinition.create(mesh, 64, 32) : LayerDefinition.create(mesh, 64, 32);
	}
}