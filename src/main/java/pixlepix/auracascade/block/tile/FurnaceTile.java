package pixlepix.auracascade.block.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import pixlepix.auracascade.main.AuraUtil;

import java.util.List;

/**
 * Created by pixlepix on 11/29/14.
 */
public class FurnaceTile extends ConsumerTile {
	public static int MAX_PROGRESS = 3;
	public static int POWER_PER_PROGRESS = 190;

	@Override
	public int getMaxProgress() {
		return MAX_PROGRESS;
	}

	@Override
	public int getPowerPerProgress() {
		return POWER_PER_PROGRESS;
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		super.readCustomNBT(nbt);
		progress = nbt.getInteger("progress");
	}

	@Override
	public boolean validItemsNearby() {
		int range = 3;
		List<EntityItem> nearbyItems = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-range, -range, -range), pos.add(range, range, range)));
		for (EntityItem entityItem : nearbyItems) {
			ItemStack stack = entityItem.getEntityItem();
			if (FurnaceRecipes.instance().getSmeltingResult(stack) != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		super.writeCustomNBT(nbt);
		progress = nbt.getInteger("progress");
	}


	@Override
	public void onUsePower() {
		//     AuraCascade.analytics.eventDesign("consumerSmelt", AuraUtil.formatLocation(this));
		int range = 3;
		List<EntityItem> nearbyItems = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-range, -range, -range), pos.add(range, range, range)));
		for (EntityItem entityItem : nearbyItems) {
			ItemStack stack = entityItem.getEntityItem();
			if (FurnaceRecipes.instance().getSmeltingResult(stack) != null) {

				//Kill the stack
				if (stack.getCount() == 0) {
					entityItem.setDead();
				} else {
					stack.shrink(1);
				}

				AuraUtil.respawnItemWithParticles(world, entityItem, FurnaceRecipes.instance().getSmeltingResult(stack).copy());

				break;
			}
		}
	}
}






