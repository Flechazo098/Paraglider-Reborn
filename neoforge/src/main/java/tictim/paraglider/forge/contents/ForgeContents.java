package tictim.paraglider.forge.contents;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.api.ParagliderAPI;
import tictim.paraglider.api.bargain.Bargain;
import tictim.paraglider.contents.Contents;
import tictim.paraglider.contents.block.GoddessStatueBlock;
import tictim.paraglider.contents.block.HornedStatueBlock;
import tictim.paraglider.contents.item.AntiVesselItem;
import tictim.paraglider.contents.item.EssenceItem;
import tictim.paraglider.contents.item.HeartContainerItem;
import tictim.paraglider.contents.item.ParagliderItem;
import tictim.paraglider.contents.item.SpiritOrbItem;
import tictim.paraglider.contents.item.StaminaVesselItem;
import tictim.paraglider.contents.recipe.CosmeticRecipe;
import tictim.paraglider.contents.recipe.SimpleBargainSerializer;
import tictim.paraglider.contents.worldgen.NetherHornedStatue;
import tictim.paraglider.contents.worldgen.TarreyTownGoddessStatue;
import tictim.paraglider.contents.worldgen.UndergroundHornedStatue;
import tictim.paraglider.forge.contents.item.ForgeParagliderItem;
import tictim.paraglider.forge.contents.loot.LootConditions;
import tictim.paraglider.forge.contents.loot.ParagliderLoot;
import tictim.paraglider.forge.contents.loot.SpawnerSpiritOrbLoot;
import tictim.paraglider.forge.contents.loot.SpiritOrbLoot;
import tictim.paraglider.forge.contents.loot.VesselLoot;

import static tictim.paraglider.api.ParagliderAPI.MODID;
import static tictim.paraglider.contents.CommonContents.*;

@SuppressWarnings("unused")
public final class ForgeContents implements Contents {
	private final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
	private final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
	private final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
	private final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);
	private final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, MODID);
	private final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOTS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);
	private final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MODID);
	private final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, MODID);
	private final DeferredRegister<StructurePieceType> PIECES = DeferredRegister.create(Registries.STRUCTURE_PIECE, MODID);
	private final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

	private final DeferredHolder<Block, Block> GODDESS_STATUE = BLOCKS.register("goddess_statue",
			() -> new GoddessStatueBlock(statueBlock()));
	private final DeferredHolder<Block, Block> KAKARIKO_GODDESS_STATUE = BLOCKS.register("kakariko_goddess_statue",
			() -> new GoddessStatueBlock(statueBlock(), kakarikoStatueTooltip()));
	private final DeferredHolder<Block, Block> GORON_GODDESS_STATUE = BLOCKS.register("goron_goddess_statue",
			() -> new GoddessStatueBlock(statueBlock().lightLevel(value -> 15), goronStatueTooltip()));
	private final DeferredHolder<Block, Block> RITO_GODDESS_STATUE = BLOCKS.register("rito_goddess_statue",
			() -> new GoddessStatueBlock(statueBlock(), ritoStatueTooltip()));
	private final DeferredHolder<Block, Block> HORNED_STATUE = BLOCKS.register("horned_statue",
			() -> new HornedStatueBlock(statueBlock()));

	private final DeferredHolder<Item, ParagliderItem> PARAGLIDER = ITEMS.register("paraglider", () -> new ForgeParagliderItem(PARAGLIDER_DEFAULT_COLOR));
	private final DeferredHolder<Item, ParagliderItem> DEKU_LEAF = ITEMS.register("deku_leaf", () -> new ForgeParagliderItem(DEKU_LEAF_DEFAULT_COLOR));
	private final DeferredHolder<Item, Item> HEART_CONTAINER = ITEMS.register("heart_container", () -> new HeartContainerItem(rareItem()));
	private final DeferredHolder<Item, Item> STAMINA_VESSEL = ITEMS.register("stamina_vessel", () -> new StaminaVesselItem(rareItem()));
	private final DeferredHolder<Item, Item> SPIRIT_ORB = ITEMS.register("spirit_orb", () -> new SpiritOrbItem(uncommonItem()));
	private final DeferredHolder<Item, Item> ANTI_VESSEL = ITEMS.register("anti_vessel", () -> new AntiVesselItem(epicItem()));
	private final DeferredHolder<Item, Item> ESSENCE = ITEMS.register("essence", () -> new EssenceItem(rareItem()));
	private final DeferredHolder<Item, BlockItem> GODDESS_STATUE_ITEM = ITEMS.register("goddess_statue", () -> new BlockItem(GODDESS_STATUE.get(), rareItem()));
	private final DeferredHolder<Item, BlockItem> KAKARIKO_GODDESS_STATUE_ITEM = ITEMS.register("kakariko_goddess_statue", () -> new BlockItem(KAKARIKO_GODDESS_STATUE.get(), rareItem()));
	private final DeferredHolder<Item, BlockItem> GORON_GODDESS_STATUE_ITEM = ITEMS.register("goron_goddess_statue", () -> new BlockItem(GORON_GODDESS_STATUE.get(), rareItem()));
	private final DeferredHolder<Item, BlockItem> RITO_GODDESS_STATUE_ITEM = ITEMS.register("rito_goddess_statue", () -> new BlockItem(RITO_GODDESS_STATUE.get(), rareItem()));
	private final DeferredHolder<Item, BlockItem> HORNED_STATUE_ITEM = ITEMS.register("horned_statue", () -> new BlockItem(HORNED_STATUE.get(), epicItem()));

	private final DeferredHolder<RecipeSerializer<?>, CosmeticRecipe.Serializer> COSMETIC_RECIPE = RECIPE_SERIALIZERS.register("cosmetic", CosmeticRecipe.Serializer::new);
	private final DeferredHolder<RecipeSerializer<?>, SimpleBargainSerializer.Simple> BARGAIN_RECIPE = RECIPE_SERIALIZERS.register("statue_bargain", SimpleBargainSerializer.Simple::new);

	private final DeferredHolder<RecipeType<?>, RecipeType<Bargain>> BARGAIN_RECIPE_TYPE = RECIPE_TYPES.register("bargain",
			() -> RecipeType.simple(ParagliderAPI.id("bargain")));

	private final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<ParagliderLoot>> PARAGLIDER_LOOT = LOOTS.register("paraglider", () -> ParagliderLoot.CODEC);
	private final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<SpiritOrbLoot>> SPIRIT_ORB_LOOT = LOOTS.register("spirit_orb", () -> SpiritOrbLoot.CODEC);
	private final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<VesselLoot>> VESSEL_LOOT = LOOTS.register("vessel", () -> VesselLoot.CODEC);
	private final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<SpawnerSpiritOrbLoot>> SPAWNER_SPIRIT_ORB_LOOT = LOOTS.register("spawner_spirit_orb", () -> SpawnerSpiritOrbLoot.CODEC);

	public final DeferredHolder<LootItemConditionType, LootItemConditionType> WITHER_DROPS_VESSEL_CONFIG_CONDITION = LOOT_CONDITIONS.register("config_wither_drops_vessel",
			() -> new LootItemConditionType(LootConditions.CODEC_WITHER_DROPS_VESSEL));
	public final DeferredHolder<LootItemConditionType, LootItemConditionType> SPIRIT_ORB_LOOTS_CONFIG_CONDITION = LOOT_CONDITIONS.register("config_spirit_orb_loots",
			() -> new LootItemConditionType(LootConditions.CODEC_SPIRIT_ORB_LOOTS));

	private <T extends Structure> DeferredHolder<StructureType<?>, StructureType<T>> structureType(String id, Codec<T> codec){
		return STRUCTURE_TYPES.register(id, () -> () -> codec);
	}
	private final DeferredHolder<StructureType<?>, StructureType<TarreyTownGoddessStatue>> TARREY_TOWN_GODDESS_STATUE = structureType("tarrey_town_goddess_statue", TarreyTownGoddessStatue.CODEC);
	private final DeferredHolder<StructureType<?>, StructureType<NetherHornedStatue>> NETHER_HORNED_STATUE = structureType("nether_horned_statue", NetherHornedStatue.CODEC);
	private final DeferredHolder<StructureType<?>, StructureType<UndergroundHornedStatue>> UNDERGROUND_HORNED_STATUE = structureType("underground_horned_statue", UndergroundHornedStatue.CODEC);

	private final DeferredHolder<StructurePieceType, StructurePieceType> TARREY_TOWN_GODDESS_STATUE_PIECE = PIECES.register("tarrey_town_goddess_statue", TarreyTownGoddessStatue::pieceType);
	private final DeferredHolder<StructurePieceType, StructurePieceType> NETHER_HORNED_STATUE_PIECE = PIECES.register("nether_horned_statue", NetherHornedStatue::pieceType);
	private final DeferredHolder<StructurePieceType, StructurePieceType> UNDERGROUND_HORNED_STATUE_PIECE = PIECES.register("underground_horned_statue", UndergroundHornedStatue::pieceType);

	private final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_TABS.register(MODID, () -> CreativeModeTab.builder()
			.icon(() -> new ItemStack(PARAGLIDER.get()))
			.title(Component.translatable("itemGroup."+MODID))
			.displayItems((features, out) -> {
				out.accept(PARAGLIDER.get());
				out.accept(DEKU_LEAF.get());
				out.accept(HEART_CONTAINER.get());
				out.accept(STAMINA_VESSEL.get());
				out.accept(SPIRIT_ORB.get());
				out.accept(ANTI_VESSEL.get());
				out.accept(ESSENCE.get());
				out.accept(GODDESS_STATUE.get());
				out.accept(KAKARIKO_GODDESS_STATUE.get());
				out.accept(GORON_GODDESS_STATUE.get());
				out.accept(RITO_GODDESS_STATUE.get());
				out.accept(HORNED_STATUE.get());
			}).build());

	public ForgeContents(IEventBus modEventBus) {
		BLOCKS.register(modEventBus);
		ITEMS.register(modEventBus);
		LOOTS.register(modEventBus);
		LOOT_CONDITIONS.register(modEventBus);
		RECIPE_SERIALIZERS.register(modEventBus);
		ATTRIBUTES.register(modEventBus);
		RECIPE_TYPES.register(modEventBus);
		STRUCTURE_TYPES.register(modEventBus);
		PIECES.register(modEventBus);
		CREATIVE_TABS.register(modEventBus);
	}

	@NotNull public DeferredRegister<Block> blocks() {
		return BLOCKS;
	}

	@Override @NotNull public ParagliderItem paraglider() {
		return PARAGLIDER.get();
	}

	@Override
	public @NotNull ParagliderItem dekuLeaf () {
		return DEKU_LEAF.get();
	}

	@Override @NotNull public Item heartContainer(){
		return HEART_CONTAINER.get();
	}
	@Override @NotNull public Item staminaVessel(){
		return STAMINA_VESSEL.get();
	}
	@Override @NotNull public Item spiritOrb(){
		return SPIRIT_ORB.get();
	}
	@Override @NotNull public Item antiVessel(){
		return ANTI_VESSEL.get();
	}
	@Override @NotNull public Item essence(){
		return ESSENCE.get();
	}
	@Override @NotNull public Block goddessStatue(){
		return GODDESS_STATUE.get();
	}
	@Override @NotNull public Block kakarikoGoddessStatue(){
		return KAKARIKO_GODDESS_STATUE.get();
	}
	@Override @NotNull public Block goronGoddessStatue(){
		return GORON_GODDESS_STATUE.get();
	}
	@Override @NotNull public Block ritoGoddessStatue(){
		return RITO_GODDESS_STATUE.get();
	}
	@Override @NotNull public Block hornedStatue(){
		return HORNED_STATUE.get();
	}
	@Override @NotNull public BlockItem goddessStatueItem(){
		return GODDESS_STATUE_ITEM.get();
	}
	@Override @NotNull public BlockItem kakarikoGoddessStatueItem(){
		return KAKARIKO_GODDESS_STATUE_ITEM.get();
	}
	@Override @NotNull public BlockItem goronGoddessStatueItem(){
		return GORON_GODDESS_STATUE_ITEM.get();
	}
	@Override @NotNull public BlockItem ritoGoddessStatueItem(){
		return RITO_GODDESS_STATUE_ITEM.get();
	}
	@Override @NotNull public BlockItem hornedStatueItem(){
		return HORNED_STATUE_ITEM.get();
	}
	@Override @NotNull public CosmeticRecipe.Serializer cosmeticRecipeSerializer(){
		return COSMETIC_RECIPE.get();
	}
	@Override @NotNull public RecipeSerializer<? extends Bargain> bargainRecipeSerializer(){
		return BARGAIN_RECIPE.get();
	}
	@Override @NotNull public RecipeType<Bargain> bargainRecipeType(){
		return BARGAIN_RECIPE_TYPE.get();
	}
	@Override @NotNull public StructureType<TarreyTownGoddessStatue> tarreyTownGoddessStatue(){
		return TARREY_TOWN_GODDESS_STATUE.get();
	}
	@Override @NotNull public StructureType<NetherHornedStatue> netherHornedStatue(){
		return NETHER_HORNED_STATUE.get();
	}
	@Override @NotNull public StructureType<UndergroundHornedStatue> undergroundHornedStatue(){
		return UNDERGROUND_HORNED_STATUE.get();
	}
	@Override @NotNull public StructurePieceType tarreyTownGoddessStatuePiece(){
		return TARREY_TOWN_GODDESS_STATUE_PIECE.get();
	}
	@Override @NotNull public StructurePieceType netherHornedStatuePiece(){
		return NETHER_HORNED_STATUE_PIECE.get();
	}
	@Override @NotNull public StructurePieceType undergroundHornedStatuePiece(){
		return UNDERGROUND_HORNED_STATUE_PIECE.get();
	}
}
