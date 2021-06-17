package com.anatawa12.simpleEconomy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "simple-economy-mod")
public class SimpleEconomy {
    public static final Logger MONEY_LOGGER = LogManager.getLogger("simple-economy-mod/money-management");

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandGetMoney());
        event.registerServerCommand(new CommandSendMoney());
        event.registerServerCommand(new CommandTakeMoney());
    }
}
