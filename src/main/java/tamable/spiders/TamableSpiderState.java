package tamable.spiders.client.renderer.entity.state;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

@OnlyIn(Dist.CLIENT)
public class TamableSpiderState extends LivingEntityRenderState {
	public EntityType type = EntityType.SPIDER;
	public AnimationState sittingAnim = new AnimationState();
	public ItemStack armor = ItemStack.EMPTY;
	public DyeColor collarColor = null;
	public boolean hasSaddle = false;
	public boolean isSitting = false;
	public boolean tamed = false;
	public boolean onBack = false;
    public float headRollAngle;
}
