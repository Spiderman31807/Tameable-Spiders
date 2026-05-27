package tameable.spiders.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.SpiderModel;

import tameable.spiders.renderer.layers.SpiderBackLayer;
import tameable.spiders.renderer.layers.SpiderBabyHead;
import tameable.spiders.entity.ModdedSpider;

public class TamableSpiderRenderer extends SpiderRenderer<ModdedSpider> {

	public TamableSpiderRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.addLayer(new SpiderBackLayer(this));
		this.addLayer(new SpiderBabyHead(this));
	}

	@Override
	protected void scale(ModdedSpider entity, PoseStack poseStack, float f) {
		float baseScale = entity.getModelScale();
		float scale = baseScale * (entity.isBaby() ? 0.5f : 1f);
		poseStack.scale(scale, scale, scale);
	}

	@Override
	public ResourceLocation getTextureLocation(ModdedSpider entity) {
		return entity.getTexture(entity.isBaby() ? 1 : 0);
	}
}