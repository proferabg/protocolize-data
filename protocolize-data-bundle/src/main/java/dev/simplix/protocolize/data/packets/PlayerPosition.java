package dev.simplix.protocolize.data.packets;

import dev.simplix.protocolize.api.Location;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

/**
 * Date: 29.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class PlayerPosition extends AbstractPacket {

    /* ServerboundMovePlayerPacket.Pos */

    public static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, 0x04),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_11_2, 0x0C),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12, MINECRAFT_1_12, 0x0E),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12_1, MINECRAFT_1_12_2, 0x0D),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x10),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_15_2, 0x11),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16, MINECRAFT_1_16_4, 0x12),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_17, MINECRAFT_1_18_2, 0x11),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19, MINECRAFT_1_19, 0x13),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_1, MINECRAFT_1_19_2, 0x14),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_3, MINECRAFT_1_19_3, 0x13),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_4, MINECRAFT_1_20_1, 0x14),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_2, MINECRAFT_1_20_2, 0x16),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_3, MINECRAFT_1_20_4, 0x17),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_21, 0x1A),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21_2, MINECRAFT_1_21_5, 0x1C),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21_6, MINECRAFT_LATEST, 0x1D)
    );

    private Location location;
    private boolean onGround;
    private boolean horizontalCollision;

    @Override
    public void read(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        this.location = new Location(buf.readDouble(), buf.readDouble(), buf.readDouble(), 0, 0);
        if(protocolVersion >= MINECRAFT_1_21_2){
            short flags = buf.readUnsignedByte();
            this.onGround = PlayerPositionLook.unpackOnGround(flags);
            this.horizontalCollision = PlayerPositionLook.unpackHorizontalCollision(flags);
        } else {
            this.onGround = buf.readBoolean();
        }
    }

    @Override
    public void write(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        buf.writeDouble(this.location.x());
        buf.writeDouble(this.location.y());
        buf.writeDouble(this.location.z());
        if(protocolVersion >= MINECRAFT_1_21_2){
            buf.writeByte(PlayerPositionLook.packFlags(this.onGround, this.horizontalCollision));
        } else {
            buf.writeBoolean(this.onGround);
        }
    }

}
