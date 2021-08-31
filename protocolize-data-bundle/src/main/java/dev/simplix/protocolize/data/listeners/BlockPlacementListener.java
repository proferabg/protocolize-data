package dev.simplix.protocolize.data.listeners;

import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.PlayerInteract;
import dev.simplix.protocolize.api.inventory.PlayerInventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.data.packets.BlockPlacement;

/**
 * Date: 31.08.2021
 *
 * @author Exceptionflug
 */
public final class BlockPlacementListener extends AbstractPacketListener<BlockPlacement> {

    public BlockPlacementListener() {
        super(BlockPlacement.class, Direction.UPSTREAM, 0);
    }

    @Override
    public void packetReceive(PacketReceiveEvent<BlockPlacement> event) {
        if (event.player() == null) {
            return;
        }
        PlayerInventory playerInventory = event.player().proxyInventory();
        ItemStack inHand = playerInventory.item(playerInventory.heldItem() + 36);
        PlayerInteract interact = new PlayerInteract(inHand, event.packet().position(), event.packet().hand(), false);
        event.player().handleInteract(interact);
        if (!event.packet().position().equals(interact.clickedBlockPosition())) {
            event.packet().position(interact.clickedBlockPosition());
            event.markForRewrite();
        }
        if (event.packet().hand() != interact.hand()) {
            event.packet().hand(interact.hand());
            event.markForRewrite();
        }
        if (inHand != null || interact.cancelled()) {
            event.cancelled(true);
        }
    }

    @Override
    public void packetSend(PacketSendEvent<BlockPlacement> packetSendEvent) {

    }

}
