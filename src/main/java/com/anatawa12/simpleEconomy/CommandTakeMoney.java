package com.anatawa12.simpleEconomy;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class CommandTakeMoney extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getCommandName() {
        return "take-money";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "command.take-money.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) throw new WrongUsageException(getCommandUsage(sender));

        int i = 0;
        final int amount = parseIntWithMin(sender, args[i++], 1);

        if (args.length < i + 2) throw new WrongUsageException(getCommandUsage(sender));

        final EntityPlayer targetPlayer;
        if ("from".equals(args[i])) {
            i++;
            targetPlayer = getPlayer(sender, args[i++]);
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        if (i != args.length) throw new WrongUsageException(getCommandUsage(sender));

        int targetHave = PlayerMoney.getMoney(targetPlayer);

        if (targetHave < amount) {
            throw new WrongUsageException("command.take-money.wrong.%s.no-much-money", targetPlayer.getCommandSenderName());
        }

        PlayerMoney.setMoney(targetPlayer, targetHave - amount);

        sender.addChatMessage(new ChatComponentTranslation("command.take-money.success.%s.%s",
                targetPlayer.getCommandSenderName(),
                targetHave - amount));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) return null; // just number

        if (sender.canCommandSenderUseCommand(3, "get-money-others")) {
            // if you have op
            switch (args.length) {
                case 2: return getListOfStringsMatchingLastWord(args, "from", "to");
                case 3:
                    if ("from".equals(args[1])) {
                        return getListOfStringsFromIterableMatchingLastWord(args, 
                                join(Arrays.asList(MinecraftServer.getServer().getAllUsernames()), "null"));
                    } else {
                        return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
                    }
                case 4:
                    return getListOfStringsMatchingLastWord(args, "to");
                case 5:
                    return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }
        } else {
            switch (args.length) {
                case 2: return getListOfStringsMatchingLastWord(args, "to");
                case 3:
                    return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }
        }
        return null;
    }

    private Iterable<String> join(List<String> asList, String value) {
        return () -> new Iterator<String>() {
            Iterator<String> iter = asList.iterator();
            String remain = value;
            @Override
            public boolean hasNext() {
                if (iter != null) {
                    if (iter.hasNext()) return true;
                    else iter = null;
                }
                return remain != null;
            }

            @Override
            public String next() {
                if (iter != null) return iter.next();
                if (remain != null) {
                    remain = null;
                    return value;
                }
                throw new NoSuchElementException();
            }
        };
    }
}
