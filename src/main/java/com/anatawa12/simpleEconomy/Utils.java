package com.anatawa12.simpleEconomy;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.UUID;

// static utils
public class Utils {
    private Utils() {
    }

    @Nonnull
    public static String getUUIDString(EntityPlayer entityPlayer) {
        UUID uniqueID = entityPlayer.getUniqueID();
        return uniqueID.toString().replace("-", "").toLowerCase(Locale.ROOT);
    }

    public static boolean hasPrivileges(ICommandSender player) {
        return player.canCommandSenderUseCommand(3, "simple-economy-privilege");
    }
}
