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

public class CommandSendMoney extends CommandBase {
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
        return "send-money";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        if (sender.canCommandSenderUseCommand(3, "get-money-others")) {
            return "command.send-money.usage.with-op";
        } else {
            return "command.send-money.usage.no-op";
        }
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) throw new WrongUsageException(getCommandUsage(sender));

        int i = 0;
        final int value = parseIntWithMin(sender, args[i++], 1);

        if (args.length < i + 2) throw new WrongUsageException(getCommandUsage(sender));

        final EntityPlayer sourcePlayer;
        if ("from".equals(args[i])) {
            if (!sender.canCommandSenderUseCommand(3, "send-money-others"))
                throw new WrongUsageException("command.send-money.wrong.no-op-to-send");
            i++;
            if (!"null".equals(args[i++])) {
                sourcePlayer = getPlayer(sender, args[i - 1]);
            } else {
                sourcePlayer = null;
            }
        } else {
            if (!(sender instanceof EntityPlayer))
                throw new WrongUsageException("command.send-money.wrong.sender-not-player");
            sourcePlayer = (EntityPlayer) sender;
        }

        if (args.length < i + 2) throw new WrongUsageException(getCommandUsage(sender));

        final EntityPlayer targetPlayer;
        if ("to".equals(args[i])) {
            i++;
            targetPlayer = getPlayer(sender, args[i++]);
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        if (i != args.length) throw new WrongUsageException(getCommandUsage(sender));

        if (sourcePlayer != null && PlayerMoney.getMoney(sourcePlayer) < value) {
            throw new WrongUsageException("command.send-money.wrong.%s.no-much-money", sourcePlayer.getCommandSenderName());
        }

        int targetHave = PlayerMoney.getMoney(targetPlayer);
        PlayerMoney.setMoney(targetPlayer, targetHave + value);

        sender.addChatMessage(new ChatComponentTranslation("command.send-money.success.%s.%s.%s.%s",
                value,
                sourcePlayer == null ? "nobody" : sourcePlayer.getCommandSenderName(),
                targetPlayer.getCommandSenderName(),
                targetHave + value));
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
