package net.minto.hoppingelytra;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HoppingElytraClient implements ClientModInitializer {
    public static final String MOD_ID = "hopping-elytra";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private boolean prevOnGround = true;
    private int startFlyDelayTicks = 0;

	@Override
	public void onInitializeClient() {
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(this::tick);
		LOGGER.info("HoppingElytra initialized");
	}

    private void tick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;

        boolean onGround = mc.player.isOnGround();
        boolean isGliding = mc.player.isGliding();
        boolean Jumping = mc.player.input.playerInput.jump();

        if (Jumping && prevOnGround && !onGround) {
            if (isGliding) {
                mc.player.jump();
                startFlyDelayTicks = 1;
            }
        }

        if (startFlyDelayTicks > 0) {
            startFlyDelayTicks--;
            if (startFlyDelayTicks == 0) {
                if (mc.player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST).getItem() == net.minecraft.item.Items.ELYTRA) {
                    mc.player.startGliding();
                    if (mc.getNetworkHandler() != null) {
                        mc.getNetworkHandler().sendPacket(
                                new net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket(mc.player, net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode.START_FALL_FLYING)
                        );
                    }
                }
            }
        }

        prevOnGround = onGround;
    }
}


