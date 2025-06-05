package tictim.paraglider.forge.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import tictim.paraglider.ParagliderMod;
import tictim.paraglider.ParagliderUtils;
import tictim.paraglider.api.ParagliderAPI;
import tictim.paraglider.api.movement.Movement;
import tictim.paraglider.bargain.BargainHandler;
import tictim.paraglider.forge.attachment.PlayerMovementAttachment;
import tictim.paraglider.impl.movement.PlayerMovement;
import tictim.paraglider.impl.movement.ServerPlayerMovement;
import tictim.paraglider.network.ParagliderNetwork;

import static tictim.paraglider.api.ParagliderAPI.MODID;
import static tictim.paraglider.api.movement.ParagliderPlayerStates.Flags.FLAG_PARAGLIDING;

@Mod.EventBusSubscriber(modid = MODID)
public final class ParagliderEventHandler{
	private ParagliderEventHandler(){}

	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getHand() == InteractionHand.OFF_HAND) {
			Movement movement = Movement.get(event.getEntity());
			if (movement.state().has(FLAG_PARAGLIDING)) {
				event.setResult(Event.Result.DENY);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerStartUseItem(LivingEntityUseItemEvent.Start event){
		if(!(event.getEntity() instanceof Player player)) return;
		Movement movement = Movement.get(player);
		if(movement.state().has(FLAG_PARAGLIDING)) event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onPlayerTickUseItem(LivingEntityUseItemEvent.Tick event){
		if(!(event.getEntity() instanceof Player player)) return;
		Movement movement = Movement.get(player);
		if(movement.state().has(FLAG_PARAGLIDING)) player.stopUsingItem();
	}

	@SubscribeEvent
	public static void onClone(PlayerEvent.Clone event){
		Player original = event.getOriginal();
		PlayerMovement m1 = PlayerMovementAttachment.get(original);
		if(m1 != null){
			PlayerMovement m2 = PlayerMovementAttachment.get(event.getEntity());
			if(m2 != null){
				m2.copyFrom(m1);
				if(event.isWasDeath()){
					m2.stamina().setStamina(m2.stamina().maxStamina());
				}
			}
		}
	}

	private static final ResourceLocation MOVEMENT_HANDLER_KEY = ParagliderAPI.id("paragliding_movement_handler");


	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event){
		if(event.phase!= TickEvent.Phase.END) return;
		if(Movement.get(event.player) instanceof PlayerMovement playerMovement)
			playerMovement.update();
	}

	@SubscribeEvent
	public static void onStartTracking(PlayerEvent.StartTracking event){
		if(!(event.getTarget() instanceof Player tracking)||!(event.getEntity() instanceof ServerPlayer player)) return;
		ParagliderNetwork.get().syncRemoteMovement(tracking, player, Movement.get(tracking).state().id());
	}

	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event){
		if(Movement.get(event.getEntity()) instanceof ServerPlayerMovement serverPlayerMovement){
			serverPlayerMovement.markForSync();
		}
	}

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event){
		if(event.phase==TickEvent.Phase.END) BargainHandler.update();
	}

	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event){
		if(event.getEntity() instanceof ServerPlayer player)
			ParagliderNetwork.get().syncStateMap(player, ParagliderMod.instance().getLocalPlayerStateMap());
	}
}
