import tameable.spiders.entity.TamableSpider;
import tameable.spiders.entity.AgeableSpider;
import tameable.spiders.animation.SittingAnimation;

public class SpiderBack<AgeableSpider extends Entity> extends HierarchicalModel<AgeableSpider> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "SpiderBack"), "main");
	private final ModelPart neck;
	private final ModelPart body;
	private final ModelPart saddle;
	private final ModelPart saddleSide;

	public SpiderBack(ModelPart root) {
		this.neck = root;
		this.body = root.getChild("body");
		this.saddle = body.getChild("saddle");
		this.saddleSide = saddle.getChild("saddle_side");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition neck = partdefinition.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, 0.0F));
		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, 9.0F));
		PartDefinition saddle = body.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(27, 5).addBox(-5.0F, 0.4F, -1.6F, 10.0F, -1.0F, 16.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -4.2F, -7.0F));
		PartDefinition saddleSide = saddle.addOrReplaceChild("saddle_side", CubeListBuilder.create().texOffs(24, -2).addBox(-10.0F, -5.0F, -5.0F, -1.0F, 10.0F, 10.0F, new CubeDeformation(0.5F)).texOffs(24, -2).addBox(1.0F, -5.0F, -5.0F, -1.0F, 10.0F, 10.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(5.0F, 5.4F, 5.4F, 1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	public ModelPart root() {
		return this.neck;
	}

	public void prepareMobModel(AgeableSpider entity, float number1, float number2, float number3) {
		saddle.visible = number1 < 0;
	}

	public void setupAnim(AgeableSpider entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		boolean sitting = ((TamableSpider) entity).isSittingPose();
		AnimationState SittingState = ((TamableSpider) entity).getState(true);
		AnimationState StandingState = ((TamableSpider) entity).getState(false);

		if(sitting)
			this.animate(SittingState, SittingAnimation.Sit, ageInTicks, 1.0F);
		if(!sitting)
			this.animate(StandingState, SittingAnimation.Stand, ageInTicks, 1.0F);
	}


	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		neck.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}