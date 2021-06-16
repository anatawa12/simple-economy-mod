package com.anatawa12.simpleEconomy;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

import java.util.List;

public class CommandGetMoney extends CommandBase {
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
        if (sender.canCommandSenderUseCommand(3, "get-money-others"))
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
                player.addChatMessage(new ChatComponentTranslation("command.get-money.success.you.%s",
                        PlayerMoney.getMoney(player)));
                break;
            }
            case 1: {
                if (!sender.canCommandSenderUseCommand(3, "get-money-others"))
                    throw new WrongUsageException("command.get-money.wrong.no-op-to-send");
                EntityPlayer player = getPlayer(sender, args[0]);
                player.addChatMessage(new ChatComponentTranslation("command.get-money.success.%s.%s",
                        player.getCommandSenderName(),
                        PlayerMoney.getMoney(player)));
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
        if (!sender.canCommandSenderUseCommand(3, "get-money-others"))
            return null;
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        return null;
    }
}
