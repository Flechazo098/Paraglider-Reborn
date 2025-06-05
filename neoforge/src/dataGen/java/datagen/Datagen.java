package datagen;

import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;

import static tictim.paraglider.api.ParagliderAPI.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Datagen{
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event){
		DataGenerator gen = event.getGenerator();
		gen.addProvider(event.includeServer(), new RecipeGen(gen.getPackOutput()));
		BlockTagGen blockTagGen = new BlockTagGen(gen.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper());
		gen.addProvider(event.includeServer(), blockTagGen);
		gen.addProvider(event.includeServer(), new ItemTagGen(gen.getPackOutput(), event.getLookupProvider(), blockTagGen.contentsGetter(), event.getExistingFileHelper()));
		gen.addProvider(event.includeServer(), new BiomeTagGen(gen.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
		gen.addProvider(event.includeServer(), new LootTableGen(gen.getPackOutput()));
		gen.addProvider(event.includeServer(), new LootModifierProvider(gen.getPackOutput()));
		gen.addProvider(event.includeServer(), new AdvancementProvider(gen.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper(), List.of(
				new AdvancementGen()
		)));
		gen.addProvider(event.includeServer(), new BargainTypeGen(gen.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
	}
}
