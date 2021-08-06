package com.anatawa12.simpleEconomy;

import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.PlayerNotFoundException;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

public abstract class MoneyCommandBase extends CommandBase {
    @Nonnull
    public static MoneyManager.Player getPlayer(@Nonnull String name)
    {
        MoneyManager.Player player = MoneyManager.getPlayerByName(name);

        if (player != null)
        {
            return player;
        }
        else
        {
            throw new PlayerNotFoundException("command.simple-money.player.not-found.%s", name);
        }
    }

    public static BigDecimal parseBigDecimalWithMinAndUnit(String str, BigDecimal min) {
        BigDecimal bd;
        try {
            bd = Utils.parseBigDecimalWithUnit(str);
        } catch (NumberFormatException e) {
            throw new NumberInvalidException("commands.generic.num.invalid", str);
        }
        if (bd.compareTo(min) < 0)
            throw new NumberInvalidException("commands.generic.num.tooSmall", bd.toPlainString(), min.toPlainString());
        return bd;
    }
}
