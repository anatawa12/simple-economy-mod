package com.anatawa12.simpleEconomy;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

import java.util.List;

public class CommandGetMoney extends MoneyCommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if (sender instanceof EntityPlayer)
            return true;
        return super.canCommandSenderUseCommand(sender);
    }

    @Override
    public String getCommandName() {
        return "get-money";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        if (Utils.hasPrivileges(sender))
            return "command.get-money.usage.with-op";
        else
            return "command.get-money.usage.no-op";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        switch (args.length) {
            case 0: {
                if (!(sender instanceof EntityPlayer))
                    throw new WrongUsageException("command.get-money.wrong.sender-not-player");
                EntityPlayer player = (EntityPlayer) sender;
                player.addChatMessage(new ChatComponentTranslation("command.get-money.success.you.%s.%s",
                        MoneyManager.getPlayerByEntity(player).getMoney(), SimpleEconomy.getUnit()));
                break;
            }
            case 1: {
                if (!Utils.hasPrivileges(sender))
                    throw new WrongUsageException("command.get-money.wrong.no-op-to-send");
                MoneyManager.Player player = getPlayer(args[0]);
                sender.addChatMessage(new ChatComponentTranslation("command.get-money.success.%s.%s.%s",
                        args[0], player.getMoney(), SimpleEconomy.getUnit()));
                break;
            }
            default: {
                throw new WrongUsageException(getCommandUsage(sender));
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (!Utils.hasPrivileges(sender))
            return null;
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        return null;
    }
}
