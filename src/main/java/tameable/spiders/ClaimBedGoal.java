package tameable.spiders.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import tameable.spiders.block.entity.BedNorthBlockEntity;
import tameable.spiders.entity.ModdedSpider;

public class ClaimBedGoal extends MoveToBlockGoal {
    private final ModdedSpider spider;

    public ClaimBedGoal(ModdedSpider spider, double speed, int range) {
        super(spider, speed, range, 6);
        this.spider = spider;
        this.verticalSearchStart = 0;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.spider.isTame() && !this.spider.isSitting() && !this.spider.hasHome() && !this.spider.isVehicle() && super.canUse();
    }

    @Override
    public void tick() {
        super.tick();
        
        if (this.isReachedTarget()) {
            this.spider.storeHome(this.blockPos);
            this.spider.setSitting(true);
        }
    }

    @Override
	protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
    	BlockEntity blockEntity  = levelReader.getBlockEntity(blockPos);
    	if (blockEntity instanceof BedNorthBlockEntity Bed) {
        	return levelReader.isEmptyBlock(blockPos.above()) && !Bed.isOccupied();
    	}
    	return false;
	}

    @Override
	public double acceptedDistance() {
        return 1.5;
    }
}