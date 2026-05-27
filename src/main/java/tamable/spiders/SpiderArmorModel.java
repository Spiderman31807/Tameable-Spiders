package tamable.spiders.client.model;

import tamable.spiders.TamableSpider;
import tamable.spiders.Animations;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.HierarchicalModel;

@OnlyIn(Dist.CLIENT)
public class SpiderArmorModel extends HierarchicalModel<Entity> {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart helmet;

	public SpiderArmorModel(ModelPart root) {
		super();
		this.root = root;
		this.head = root.getChild("head");
		this.helmet = head.getChild("helmet");
		root.getChild("body1").skipDraw = true;
		this.head.skipDraw = true;
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
		if (entity instanceof TamableSpider spider) {
			this.root.getChild("body1").setPos(0f, 15f, 9f);
			this.root.getChild("body1").setRotation(0f, 0f, 0f);
			this.animate(spider.getSittingAnimation(), spider.isInSittingPose() ? Animations.Sit : Animations.Stand, ageInTicks, 1f);
			float tiltAngle = spider.getHeadRollAngle(ageInTicks);
			float cosTilt = Mth.cos(tiltAngle);
			float sinTilt = Mth.sin(tiltAngle);
			float newX = headYaw * cosTilt + headPitch * sinTilt;
			float newY = headPitch * cosTilt - headYaw * sinTilt;
			this.head.xRot = newX * ((float) Math.PI / 180F);
			this.head.yRot = newY * ((float) Math.PI / 180F);
			this.head.zRot = tiltAngle;
		}
	}

	public static LayerDefinition createLayer(boolean isCave) {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition definition = mesh.getRoot();
		PartDefinition head = definition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 4).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, 15.0F, -3.0F));
		PartDefinition body = definition.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F), PartPose.offset(0.0F, 15.0F, 9.0F));
		PartDefinition helmet = head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(32, 0).addBox(-4, -4, -8, 8, 7, 8, new CubeDeformation(0.75F)), PartPose.offset(0f, 0f, 0f));
		PartDefinition armor = body.addOrReplaceChild("armor", CubeListBuilder.create().texOffs(0, 12).addBox(-5, -4, -6, 10, 8, 12, new CubeDeformation(0.75F)), PartPose.offset(0f, 0f, 0f));
		return !isCave ? LayerDefinition.create(mesh, 64, 32) : LayerDefinition.create(mesh, 64, 32);
	}
}