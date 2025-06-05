package tictim.paraglider.forge;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public final class ParagliderUtilsImpl{
	private ParagliderUtilsImpl(){}

	public static boolean canBreatheUnderwater(@NotNull Player player){
		if(player.hasEffect(MobEffects.WATER_BREATHING)) return true;
		if(player.onGround()){
			if(!player.canDrownInFluidType(player.getEyeInFluidType())||player.level()
					.getBlockState(new BlockPos((int)player.getX(), (int)player.getEyeY(), (int)player.getZ()))
					.is(Blocks.BUBBLE_COLUMN)) return true;
		}

		ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
		if(!head.isEmpty()){
			if(head.getItem()==Items.TURTLE_HELMET) return true;
			else if(head.getEnchantmentLevel(Enchantments.AQUA_AFFINITY)>0) return true;
		}
		ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
		return !feet.isEmpty()&&feet.getEnchantmentLevel(Enchantments.DEPTH_STRIDER)>0;
	}

	public static boolean hasTag(@NotNull Block block, @NotNull TagKey<Block> tagKey){
		Optional<HolderSet.Named<Block>> tag = BuiltInRegistries.BLOCK.getTag(tagKey);
		return tag.isPresent() && tag.get().contains(BuiltInRegistries.BLOCK.wrapAsHolder(block));
	}

	@NotNull public static Item getItem(@NotNull ResourceLocation id){
		return Objects.requireNonNullElse(BuiltInRegistries.ITEM.get(id), Items.AIR);
	}

	public static @NotNull ResourceLocation getKey(@NotNull Item item){
		return BuiltInRegistries.ITEM.getKey(item);
	}

	@NotNull public static Block getBlock(@NotNull ResourceLocation id){
		return Objects.requireNonNullElse(BuiltInRegistries.BLOCK.get(id), Blocks.AIR);
	}

	public static void forRemainingItem(@NotNull ItemStack stack, @NotNull Consumer<@NotNull ItemStack> forRemainingItem){
		if(stack.hasCraftingRemainingItem()){
			forRemainingItem.accept(stack.getCraftingRemainingItem());
		}
	}

	@OnlyIn(Dist.CLIENT)
	@NotNull public static InputConstants.Key getKey(@NotNull KeyMapping keyMapping){
		return keyMapping.getKey();
	}

	@OnlyIn(Dist.CLIENT)
	public static boolean isActiveAndMatches(@NotNull KeyMapping keyMapping, @NotNull InputConstants.Key key){
		return keyMapping.isActiveAndMatches(key);
	}

	public static boolean isClient(){
		return FMLEnvironment.dist.isClient();
	}
}
