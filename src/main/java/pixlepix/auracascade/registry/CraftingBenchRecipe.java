package pixlepix.auracascade.registry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CraftingBenchRecipe extends ThaumicTinkererRecipe {
	private final ItemStack output;
	private final Object[] stuff;
	public IRecipe iRecipe;

	public CraftingBenchRecipe(ItemStack output, Object... stuff) {
		this.output = output;
		this.stuff = stuff;
	}

	@Override
	public void registerRecipe() {
		iRecipe = GameRegistry.addShapedRecipe(output, stuff);
	}
}