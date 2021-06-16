package com.anatawa12.simpleEconomy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;

@Mod(modid = "simple-economy-mod")
public class SimpleEconomy {
    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
            if (!PlayerMoney.identifier.equals(event.entity.registerExtendedProperties(
                    PlayerMoney.identifier, new PlayerMoney()))) {
                throw new IllegalStateException("duplicated extended prop name: " + PlayerMoney.identifier);
            }
        }
    }
}
