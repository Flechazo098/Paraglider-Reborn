package tictim.paraglider.forge.attachment;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.paraglider.api.ParagliderAPI;
import tictim.paraglider.api.Serde;
import tictim.paraglider.api.stamina.Stamina;
import tictim.paraglider.api.vessel.VesselContainer;
import tictim.paraglider.impl.movement.PlayerMovement;
import tictim.paraglider.impl.movement.ServerPlayerMovement;
import tictim.paraglider.impl.stamina.NullStamina;
import tictim.paraglider.impl.vessel.NullVesselContainer;

import java.util.function.Supplier;

public final class PlayerMovementAttachment {
    // DeferredRegister for attachment types
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ParagliderAPI.MODID);

    // PlayerMovement attachment with serialization support
    public static final Supplier<AttachmentType<PlayerMovement>> PLAYER_MOVEMENT =
            ATTACHMENT_TYPES.register("player_movement", () ->
                    AttachmentType.builder(PlayerMovementAttachment::createDefaultMovement)
                            .serialize(new PlayerMovementSerializer())
                            .copyOnDeath()
                            .build()
            );

    /**
     * Register the attachment types to the mod bus
     */
    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
    }

    /**
     * Get PlayerMovement from a player, creating default if not present
     */
    @NotNull
    public static PlayerMovement get(@NotNull Player player) {
        return player.getData(PLAYER_MOVEMENT);
    }

    /**
     * Set PlayerMovement for a player
     */
    public static void set(@NotNull Player player, @NotNull PlayerMovement movement) {
        player.setData(PLAYER_MOVEMENT, movement);
    }

    /**
     * Check if player has PlayerMovement data
     */
    public static boolean has(@NotNull Player player) {
        return player.hasData(PLAYER_MOVEMENT);
    }

    /**
     * Create default PlayerMovement instance
     */
    @NotNull
    private static PlayerMovement createDefaultMovement() {
        // Return a new ServerPlayerMovement instance
        // Note: A ServerPlayer parameter is required here, but we do not have a player object when creating the default instance
        // Therefore this instance will be replaced with the correct instance when actually used
        return new ServerPlayerMovement((ServerPlayer) null) {
            // Override the method to avoid NPE, as this is just a temporary instance
            @Override
            public void update() {
                // Empty implementation to avoid NPE
            }

            @Override
            @NotNull
            protected Stamina createStamina() {
                return NullStamina.get();
            }

            @Override
            @NotNull
            protected VesselContainer createVesselContainer() {
                return NullVesselContainer.get();
            }
        };
    }

    /**
     * Serializer for PlayerMovement that implements Serde
     */
    private static class PlayerMovementSerializer implements IAttachmentSerializer<CompoundTag, PlayerMovement> {
        @Override
        public PlayerMovement read(IAttachmentHolder holder, @NotNull CompoundTag tag) {
            PlayerMovement movement = createDefaultMovement();
            if (movement instanceof Serde serde) {
                serde.read(tag);
            }
            return movement;
        }

        @Override
        public @Nullable CompoundTag write(@NotNull PlayerMovement movement) {
            if (movement instanceof Serde serde) {
                return serde.write();
            }
            return new CompoundTag();
        }
    }
}