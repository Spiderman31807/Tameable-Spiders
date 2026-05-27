package tamable.spiders.client.renderer.entity.layers;

import tamable.spiders.client.renderer.entity.state.TamableSpiderState;
import tamable.spiders.client.model.SpiderSaddleModel;

import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.SpiderModel;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class SpiderSaddleLayer extends RenderLayer<TamableSpiderState, SpiderModel> {
	private static final ModelLayerLocation normalLayer = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_saddle"), "main");
	private static final ModelLayerLocation caveLayer = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_saddle"), "cave");
	private static final ModelLayerLocation armorLayer = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_saddle"), "armor");
	private static final ModelLayerLocation caveArmorLayer = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_saddle"), "cave_armor");
	private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("tamable_spiders", "textures/entities/spider_saddle.png");
	private final SpiderSaddleModel defaultModel;
	private final SpiderSaddleModel caveModel;
	private final SpiderSaddleModel armorModel;
	private final SpiderSaddleModel caveArmorModel;

	public SpiderSaddleLayer(RenderLayerParent<TamableSpiderState, SpiderModel> parent, EntityModelSet modelSet) {
		super(parent);
		this.defaultModel = new SpiderSaddleModel(modelSet.bakeLayer(normalLayer));
		this.armorModel = new SpiderSaddleModel(modelSet.bakeLayer(armorLayer));
		this.caveModel = new SpiderSaddleModel(modelSet.bakeLayer(caveLayer));
		this.caveArmorModel = new SpiderSaddleModel(modelSet.bakeLayer(caveArmorLayer));
	}

	public void render(PoseStack pose, MultiBufferSource buffer, int packedLight, TamableSpiderState state, float float1, float float2) {
		if (state.hasSaddle) {
			boolean hasArmor = state.armor.isEmpty();
			SpiderSaddleModel model = state.type == EntityType.CAVE_SPIDER ? (hasArmor ? this.caveArmorModel : this.caveModel) : (hasArmor ? this.defaultModel : this.armorModel);
			model.setupAnim(state);
			VertexConsumer vertex = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
			model.renderToBuffer(pose, vertex, packedLight, OverlayTexture.NO_OVERLAY, -1);
		}
	}
}
