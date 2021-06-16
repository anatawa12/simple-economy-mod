package com.anatawa12.simpleEconomy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerMoney implements IExtendedEntityProperties {
    public static String identifier = "com.anatawa12.simpleEconomy.PlayerMoney";
    private int money;

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        compound.setInteger(identifier, money);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        money = compound.getInteger(identifier);
    }

    @Override
    public void init(Entity entity, World world) {
    }

    public static int getMoney(EntityPlayer player) {
        if (player.worldObj.isRemote)
            throw new IllegalStateException("get money");
        PlayerMoney money = (PlayerMoney)player.getExtendedProperties(identifier);
        return money.money;
    }

    public static void setMoney(EntityPlayer player, int newMoney) {
        if (player.worldObj.isRemote)
            throw new IllegalStateException("get money");
        PlayerMoney money = (PlayerMoney)player.getExtendedProperties(identifier);
        money.money = newMoney;
    }
}
