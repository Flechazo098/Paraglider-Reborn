package datagen;

import datagen.builder.CosmeticRecipeBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import tictim.paraglider.api.ParagliderAPI;
import tictim.paraglider.contents.Contents;
import tictim.paraglider.contents.ParagliderAdvancements;
import tictim.paraglider.contents.ParagliderTags;

import java.util.function.Consumer;

import static net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems;
import static net.minecraft.advancements.critereon.ItemPredicate.Builder.item;
import static tictim.paraglider.api.ParagliderAPI.MODID;

public class AdvancementGen extends FabricAdvancementProvider{
	protected AdvancementGen(FabricDataOutput output){
		super(output);
	}


	@Override public void generateAdvancement(Consumer<AdvancementHolder> consumer){
		Contents contents = Contents.get();
		AdvancementHolder root = advancement(
				new ItemStack(contents.paraglider()),
				"advancement.paraglider",
				ParagliderAPI.id("textures/gui/advancement_background.png"),
				AdvancementType.TASK,
				false
		)
				.addCriterion("crafting_table", hasItems(Blocks.CRAFTING_TABLE))
				.save(consumer, MODID+":root");
		AdvancementHolder paraglider = advancement(
				new ItemStack(contents.paraglider()),
				"advancement.paraglider.paraglider",
				AdvancementType.GOAL
		)
				.parent(root) 
				.addCriterion("paraglider", hasItems(item().of(ParagliderTags.PARAGLIDERS).build()))
				.save(consumer, MODID+":paraglider");
		AdvancementHolder prayToTheGoddess = advancement(
				new ItemStack(contents.goddessStatue()),
				"advancement.paraglider.pray_to_the_goddess",
				AdvancementType.GOAL
		)
				.parent(root)
				.addCriterion("bargain", CosmeticRecipeBuilder.impossible())
				.save(consumer, ParagliderAdvancements.PRAY_TO_THE_GODDESS.toString());
		AdvancementHolder statuesBargain = advancement(
				new ItemStack(contents.hornedStatue()),
				"advancement.paraglider.statues_bargain",
				AdvancementType.GOAL
		)
				.parent(root)
				.addCriterion("bargain", CosmeticRecipeBuilder.impossible())
				.save(consumer, ParagliderAdvancements.STATUES_BARGAIN.toString());
		AdvancementHolder allVessels = advancement(
				new ItemStack(contents.heartContainer()),
				"advancement.paraglider.all_vessels",
				AdvancementType.CHALLENGE
		)
				.parent(root)
				.addCriterion("code_triggered", CosmeticRecipeBuilder.impossible())
				.save(consumer, ParagliderAdvancements.ALL_VESSELS.toString());
	}

	private Advancement.Builder advancement(ItemStack stack,
											String display,
											AdvancementType advancementType) {
		return advancement(stack, display, null, advancementType, true);
	}


	private Advancement.Builder advancement(ItemStack stack,
											String display,
											@Nullable ResourceLocation background,
											AdvancementType advancementType,
											boolean showToast) {
		return Advancement.Builder.advancement().display(stack,
				Component.translatable("advancement." + MODID + "." + display),
				Component.translatable("advancement." + MODID + "." + display + ".desc"),
				background,
				advancementType,
				showToast,
				false,
				false);
	}
}