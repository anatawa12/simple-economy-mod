package com.anatawa12.simpleEconomy;

import net.minecraft.command.CommandBase;
import net.minecraft.command.PlayerNotFoundException;

import javax.annotation.Nonnull;

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
}
