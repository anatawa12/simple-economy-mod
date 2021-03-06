package com.anatawa12.simpleEconomy;

import io.netty.buffer.ByteBuf;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

// static utils
public class Utils {
    private Utils() {
    }

    public static boolean hasPrivileges(ICommandSender player) {
        return player.canCommandSenderUseCommand(3, "simple-economy-privilege");
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static EntityPlayerMP findPlayer(UUID uuid) {
        for (EntityPlayerMP playerMP : (List<EntityPlayerMP>) MinecraftServer.getServer()
                .getConfigurationManager().playerEntityList) {
            if (playerMP.getUniqueID().equals(uuid))
                return playerMP;
        }
        return null;
    }

    @Nonnull
    public static NBTTagCompound encodeUuid(UUID s) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setLong("M", s.getMostSignificantBits());
        compound.setLong("L", s.getLeastSignificantBits());
        return compound;
    }

    @Nonnull
    public static UUID decodeUuid(NBTTagCompound compound) {
        return new UUID(
                compound.getLong("M"),
                compound.getLong("L"));
    }

    public static void writeString(ByteBuf buf, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    public static String readString(ByteBuf buf) {
        byte[] bytes = new byte[buf.readInt()];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String toStringForGui(MoneyManager.Player player) {
        if (player.getName() == null) {
            return "uuid:" + player.getUUID().toString().replace("-", "").substring(0, 12);
        } else {
            return player.getName();
        }
    }
}
