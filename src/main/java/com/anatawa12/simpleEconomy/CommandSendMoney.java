package com.anatawa12.simpleEconomy;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.anatawa12.simpleEconomy.SimpleEconomy.MONEY_LOGGER;

public class CommandSendMoney extends MoneyCommandBase {
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
        if (Utils.hasPrivileges(sender)) {
            return "command.send-money.usage.with-op";
        } else {
            return "command.send-money.usage.no-op";
        }
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) throw new WrongUsageException(getCommandUsage(sender));

        int i = 0;
        final BigDecimal value = parseBigDecimalWithMinAndUnit(args[i++], BigDecimal.ONE);

        if (args.length < i + 2) throw new WrongUsageException(getCommandUsage(sender));

        final MoneyManager.Player sourcePlayer;
        if ("from".equals(args[i])) {
            if (!Utils.hasPrivileges(sender))
                throw new WrongUsageException("command.send-money.wrong.no-op-to-send");
            i++;
            if (!"-".equals(args[i++])) {
                sourcePlayer = getPlayer(args[i - 1]);
            } else {
                sourcePlayer = null;
            }
        } else {
            if (!(sender instanceof EntityPlayer))
                throw new WrongUsageException("command.send-money.wrong.sender-not-player");
            sourcePlayer = MoneyManager.getPlayerByEntity((EntityPlayer) sender);
        }

        if (args.length < i + 2) throw new WrongUsageException(getCommandUsage(sender));

        final MoneyManager.Player targetPlayer;
        if ("to".equals(args[i])) {
            i++;
            targetPlayer = getPlayer(args[i++]);
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        if (i != args.length) throw new WrongUsageException(getCommandUsage(sender));

        if (sourcePlayer != null && sourcePlayer.getMoney().compareTo(value) < 0) {
            throw new WrongUsageException("command.send-money.wrong.%s.no-much-money", sourcePlayer.getName());
        }

        targetPlayer.addMoney(value);

        sender.addChatMessage(new ChatComponentTranslation("command.send-money.success.%s.%s.%s.%s",
                SimpleEconomy.getUM(value),
                sourcePlayer == null ? "nobody" : sourcePlayer.getName(),
                targetPlayer.getName(),
                SimpleEconomy.getUM(targetPlayer.getMoney())));

        MONEY_LOGGER.info("{} sent {} from {} to {}", sender, value, sourcePlayer, targetPlayer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) return null; // just number

        if (Utils.hasPrivileges(sender)) {
            // if you have op
            switch (args.length) {
                case 2:
                    return getListOfStringsMatchingLastWord(args, "from", "to");
                case 3:
                    if ("from".equals(args[1])) {
                        return getListOfStringsFromIterableMatchingLastWord(args,
                                join(Arrays.asList(MinecraftServer.getServer().getAllUsernames()), "-"));
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
                case 2:
                    return getListOfStringsMatchingLastWord(args, "to");
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
