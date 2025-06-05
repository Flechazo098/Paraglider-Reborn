package tictim.paraglider.forge.contents.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.config.Cfg;
import tictim.paraglider.contents.item.ParagliderItem;

import java.util.Optional;

public class ForgeParagliderItem extends ParagliderItem{
	public ForgeParagliderItem(int defaultColor){
		super(defaultColor);
	}

	@Override public int getMaxDamage(@NotNull ItemStack stack){
		return Cfg.get().paragliderDurability();
	}
	@Override public boolean canBeDepleted(){
		return Cfg.get().paragliderDurability()>0;
	}
	@Override public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair){
		Optional<HolderSet.Named<Item>> leatherTag = BuiltInRegistries.ITEM.getTag(Tags.Items.LEATHER);
		return leatherTag.isPresent() && leatherTag.get().contains(BuiltInRegistries.ITEM.wrapAsHolder(repair.getItem()));
	}

	@Override public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged){
		return slotChanged||oldStack.getItem()!=newStack.getItem()||isParagliding(oldStack)!=isParagliding(newStack);
	}
}