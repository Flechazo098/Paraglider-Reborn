package datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import tictim.paraglider.api.ParagliderAPI;
import tictim.paraglider.contents.Contents;
import tictim.paraglider.contents.ParagliderAdvancements;
import tictim.paraglider.contents.ParagliderTags;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import static net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems;
import static net.minecraft.advancements.critereon.ItemPredicate.Builder.item;
import static tictim.paraglider.api.ParagliderAPI.MODID;

public class AdvancementGen implements AdvancementProvider.AdvancementGenerator {

	protected Consumer<AdvancementHolder> saver;
	protected HolderLookup.Provider registries;
	protected ExistingFileHelper existingFileHelper;

	private static MutableComponent text(String name, String type) {
		return Component.translatable("advancement." + MODID + "." + name + "." + type);
	}

	public static MutableComponent title(String name) {
		return text(name, "title");
	}

	public static MutableComponent descr(String name) {
		return text(name, "description");
	}

	@Override
	public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
		this.registries = registries;
		this.saver = saver;
		this.existingFileHelper = existingFileHelper;
		this.start();
	}

	private void start() {
		Contents contents = Contents.get();

		AdvancementHolder root = this.add(advancement(
				new ItemStack(contents.paraglider()),
				"paraglider",
				ParagliderAPI.id("textures/gui/advancement_background.png"),
				AdvancementType.TASK,
				false,
				false
		)
				.addCriterion("crafting_table", hasItems(Blocks.CRAFTING_TABLE))
				.build(new ResourceLocation(MODID, "root")));

		AdvancementHolder paraglider = this.add(advancement(
				new ItemStack(contents.paraglider()),
				"paraglider.paraglider",
				AdvancementType.GOAL
		)
				.parent(root)
				.addCriterion("paraglider", hasItems(item().of(ParagliderTags.PARAGLIDERS).build()))
				.build(new ResourceLocation(MODID, "paraglider")));

		AdvancementHolder prayToTheGoddess = this.add(advancement(
				new ItemStack(contents.goddessStatue()),
				"pray_to_the_goddess",
				AdvancementType.GOAL
		)
				.parent(root)
				.addCriterion("bargain", impossible())
				.build(new ResourceLocation(ParagliderAdvancements.PRAY_TO_THE_GODDESS.toString())));

		AdvancementHolder statuesBargain = this.add(advancement(
				new ItemStack(contents.hornedStatue()),
				"statues_bargain",
				AdvancementType.GOAL
		)
				.parent(root)
				.addCriterion("bargain", impossible())
				.build(new ResourceLocation(ParagliderAdvancements.STATUES_BARGAIN.toString())));

		AdvancementHolder allVessels = this.add(advancement(
				new ItemStack(contents.heartContainer()),
				"all_vessels",
				AdvancementType.CHALLENGE
		)
				.parent(root)
				.addCriterion("code_triggered", impossible())
				.build(new ResourceLocation(ParagliderAdvancements.ALL_VESSELS.toString())));
	}

	private AdvancementHolder add(AdvancementHolder holder) {
		this.saver.accept(holder);
		return holder;
	}

	public static Criterion<ImpossibleTrigger.TriggerInstance> impossible() {
		return new Criterion<>(new ImpossibleTrigger(), new ImpossibleTrigger.TriggerInstance());
	}

	private Advancement.Builder advancement(ItemStack stack,
											String display,
											AdvancementType frameType) {
		return advancement(stack, display, null, frameType, true, true);
	}

	private Advancement.Builder advancement(ItemStack stack,
											String display,
											@Nullable ResourceLocation background,
											AdvancementType frameType,
											boolean showToast,
											boolean announceToChat) {
		return Advancement.Builder.advancement().display(stack,
				Component.translatable("advancement." + MODID + "." + display),
				Component.translatable("advancement." + MODID + "." + display + ".desc"),
				background,
				frameType,
				showToast,
				announceToChat,
				false);
	}
}