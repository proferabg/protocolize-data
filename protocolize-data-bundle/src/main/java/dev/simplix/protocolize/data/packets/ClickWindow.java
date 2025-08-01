package dev.simplix.protocolize.data.packets;

import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.item.HashedStack;
import dev.simplix.protocolize.api.item.HashedStackSerializer;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.item.ItemStackSerializer;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.util.DebugUtil;
import dev.simplix.protocolize.api.util.Either;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.api.util.exception.ExceptionUtil;
import dev.simplix.protocolize.api.util.exception.ProtocolizeException;
import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

/**
 * Date: 27.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j(topic = "Protocolize")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class ClickWindow extends AbstractPacket {

    /* ServerboundContainerClickPacket */

    public static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, 0x0E),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_11_2, 0x07),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12, MINECRAFT_1_12, 0x08),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12_1, MINECRAFT_1_12_2, 0x07),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x08),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_16_4, 0x09),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_17, MINECRAFT_1_18_2, 0x08),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19, MINECRAFT_1_19, 0x0A),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_1, MINECRAFT_1_19_2, 0x0B),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_3, MINECRAFT_1_19_3, 0x0A),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_4, MINECRAFT_1_20_1, 0x0B),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_2, MINECRAFT_1_20_4, 0x0D),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_21, 0x0E),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21_2, MINECRAFT_1_21_5, 0x10),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21_6, MINECRAFT_LATEST, 0x11)
    );

    private Map<Short, Either<ItemStack, HashedStack>> slotData = new HashMap<>();
    private int windowId;
    private int actionNumber;
    private short slot;
    private byte button;
    private int mode;
    private Either<ItemStack, HashedStack> itemStack;

    /**
     * @since 1.7.1-SNAPSHOT protocol 756
     */
    private int stateId;

    // skip decoding and save raw packet data
    boolean skipDecoding = false;
    byte[] packetData = null;

    @Override
    public void read(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        readEntirePacket(buf);
        StringBuilder sb = new StringBuilder();
        sb.append("ClickWindow:");
        try {
            this.windowId = (protocolVersion >= MINECRAFT_1_21_2) ? ProtocolUtil.readVarInt(buf) : buf.readUnsignedByte();
            sb.append("\n    Window ID: 0x").append(Integer.toHexString(this.windowId).toUpperCase());
            if (protocolVersion >= MINECRAFT_1_17_1) {
                this.stateId = ProtocolUtil.readVarInt(buf);
                sb.append("\n    State ID: 0x").append(Integer.toHexString(this.stateId).toUpperCase());
            }
            this.slot = buf.readShort();
            sb.append("\n    Slot: 0x").append(Integer.toHexString(this.slot).toUpperCase());
            this.button = buf.readByte();
            sb.append("\n    Button: 0x").append(Integer.toHexString(this.button).toUpperCase());
            if (protocolVersion < MINECRAFT_1_17) {
                this.actionNumber = buf.readShort();
                sb.append("\n    Action: 0x").append(Integer.toHexString(this.actionNumber).toUpperCase());
            }
            if (protocolVersion == MINECRAFT_1_8) {
                this.mode = buf.readByte();
            } else {
                this.mode = ProtocolUtil.readVarInt(buf);
            }
            sb.append("\n    Mode: 0x").append(Integer.toHexString(this.mode).toUpperCase());
            if (protocolVersion >= MINECRAFT_1_17) {
                int length = ProtocolUtil.readVarInt(buf);
                sb.append("\n    Slots: 0x").append(Integer.toHexString(length).toUpperCase());
                for (int i = 0; i < length; i++) {
                    if(protocolVersion >= MINECRAFT_1_21_5){
                        this.slotData.put(buf.readShort(), Either.right(HashedStackSerializer.read(buf, protocolVersion)));
                    } else {
                        this.slotData.put(buf.readShort(), Either.left(ItemStackSerializer.read(buf, protocolVersion)));
                    }
                }
            }
            if(protocolVersion >= MINECRAFT_1_21_5){
                this.itemStack = Either.right(HashedStackSerializer.read(buf, protocolVersion));
            } else {
                this.itemStack = Either.left(ItemStackSerializer.read(buf, protocolVersion));
            }
        } catch (Exception e) {
            if(DebugUtil.enabled) log.info(sb.toString());
            if(ExceptionUtil.getRootCause(e) instanceof ProtocolizeException){
                log.error("Skipping decoding ClickWindow packet: {}", e.getMessage());
            } else {
                log.error("Skipping decoding ClickWindow packet", e);
            }
            skipDecoding = true;
        }
    }

    @Override
    public void write(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        if(packetData != null && skipDecoding){
            buf.writeBytes(packetData);
            return;
        }

        if(protocolVersion >= MINECRAFT_1_21_2) {
            ProtocolUtil.writeVarInt(buf, this.windowId);
        } else {
            buf.writeByte(this.windowId & 0xFF);
        }
        if (protocolVersion >= MINECRAFT_1_17_1) {
            ProtocolUtil.writeVarInt(buf, this.stateId);
        }
        buf.writeShort(this.slot);
        buf.writeByte(this.button);
        if (protocolVersion < MINECRAFT_1_17) {
            buf.writeShort(this.actionNumber);
        }
        if (protocolVersion == MINECRAFT_1_8) {
            buf.writeByte(this.mode);
        } else {
            ProtocolUtil.writeVarInt(buf, this.mode);
        }
        if (protocolVersion >= MINECRAFT_1_17) {
            ProtocolUtil.writeVarInt(buf, this.slotData.size());
            for (short slot : this.slotData.keySet()) {
                buf.writeShort(slot);
                if(protocolVersion >= MINECRAFT_1_21_5){
                    HashedStackSerializer.write(buf, this.slotData.get(slot).getRight(), protocolVersion);
                } else {
                    ItemStackSerializer.write(buf, this.slotData.get(slot).getLeft(), protocolVersion);
                }
            }
        }
        if(protocolVersion >= MINECRAFT_1_21_5){
            HashedStackSerializer.write(buf, this.itemStack.getRight(), protocolVersion);
        } else {
            if (this.itemStack.isLeft() && this.itemStack.getLeft() == null) {
                ItemStackSerializer.write(buf, ItemStack.NO_DATA, protocolVersion);
            } else {
                ItemStackSerializer.write(buf, this.itemStack.getLeft(), protocolVersion);
            }
        }
    }

    public ClickType clickType() {
        return ClickType.getType(this.mode, this.button);
    }

    public void clickType(ClickType clickType) {
        this.mode = clickType.mode();
        this.button = (byte) clickType.button();
    }

    private void readEntirePacket(ByteBuf buf){
        int index = buf.readerIndex();
        packetData = new byte[buf.readableBytes()];
        buf.readBytes(packetData);
        buf.readerIndex(index);
    }

}
