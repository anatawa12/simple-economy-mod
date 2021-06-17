package com.anatawa12.simpleEconomy;

import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.UUID;

// static utils
public class Utils {
    private Utils() {
    }

    public static boolean hasPrivileges(ICommandSender player) {
        return player.canCommandSenderUseCommand(3, "simple-economy-privilege");
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
}
