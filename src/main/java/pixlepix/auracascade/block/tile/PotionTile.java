package pixlepix.auracascade.block.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import pixlepix.auracascade.AuraCascade;
import pixlepix.auracascade.main.AuraUtil;
import pixlepix.auracascade.network.PacketBurst;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by localmacaccount on 2/9/15.
 */
public class PotionTile extends ConsumerTile {
	public static int MAX_PROGRESS = 25;
	public static int POWER_PER_PROGRESS = 500;

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
			ItemStack smeltingResult = getBrewResult(stack);
			if (smeltingResult != null) {
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
		//    AuraCascade.analytics.eventDesign("consumerBrew", AuraUtil.formatLocation(this));
		int range = 3;
		List<EntityItem> nearbyItems = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-range, -range, -range), pos.add(range, range, range)));
		for (EntityItem entityItem : nearbyItems) {
			ItemStack stack = entityItem.getEntityItem();
			ItemStack smeltingResult = getBrewResult(stack);
			if (smeltingResult != null) {

				//Kill the stack
				if (stack.getCount() == 0) {
					entityItem.setDead();
				} else {
					stack.shrink(1);
				}

				EntityItem newEntity = new EntityItem(world, entityItem.posX, entityItem.posY, entityItem.posZ, smeltingResult.copy());

				AuraUtil.setItemDelay(newEntity, AuraUtil.getItemDelay(entityItem));
				newEntity.motionX = entityItem.motionX;
				newEntity.motionY = entityItem.motionY;
				newEntity.motionZ = entityItem.motionZ;

				world.spawnEntity(newEntity);

				AuraCascade.proxy.networkWrapper.sendToAllAround(new PacketBurst(6, newEntity.posX, newEntity.posY, newEntity.posZ), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32));

				break;
			}
		}
	}

	public ItemStack getBrewResult(ItemStack stack) {
		if (stack.getItem() == Items.POTIONITEM) {
			int meta = stack.getItemDamage();
			Integer[] basePotions = new Integer[]{8193, 8194, 8195, 8196, 8197, 8198, 8200, 8201, 8202, 8204, 8205, 8206};
			int newMeta = -1;
			if (meta == 0) {
				newMeta = 16;
			} else if (meta == 16) {
				newMeta = basePotions[new Random().nextInt(basePotions.length)];
			} else if (Arrays.asList(basePotions).contains(meta)) {
				//Apply redstone or glowstone
				newMeta = meta ^ (1 << (new Random().nextBoolean() ? 5 : 6));
			}
			if (newMeta != -1) {
				return new ItemStack(Items.POTIONITEM, 1, newMeta);
			}
		}
		return null;
	}
}