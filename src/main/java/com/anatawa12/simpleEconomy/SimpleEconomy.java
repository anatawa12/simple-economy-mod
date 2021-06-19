package com.anatawa12.simpleEconomy;

import com.anatawa12.simpleEconomy.gui.GuiHandler;
import com.anatawa12.simpleEconomy.network.NetworkHandler;
import com.anatawa12.simpleEconomy.network.SendPlayersMoney;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod(modid = "simple-economy-mod")
public class SimpleEconomy {
    public static final Logger MONEY_LOGGER = LogManager.getLogger("simple-economy-mod/money-management");
    @SideOnly(Side.CLIENT)
    public static long clientUsersMoney;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        GameRegistry.registerTileEntity(CashBoxTileEntity.class, CashBoxTileEntity.class.getName());
        GameRegistry.registerBlock(CashBoxBlock.INSTANCE, "cash_block");
        NetworkHandler.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandGetMoney());
        event.registerServerCommand(new CommandSendMoney());
        event.registerServerCommand(new CommandTakeMoney());
    }

    private static final Set<UUID> syncRequestedUuids = new HashSet<>();

    @SubscribeEvent
    public void onPreInit(TickEvent.ServerTickEvent event) {
        for (UUID syncPlayerUuid : syncRequestedUuids) {
            EntityPlayerMP playerMP = Utils.findPlayer(syncPlayerUuid);
            if (playerMP != null) {
                MoneyManager.Player player = MoneyManager.getPlayerByEntity(playerMP);
                NetworkHandler.sendToClient(new SendPlayersMoney(player.getMoney()), playerMP);
            }
        }
    }

    public static void requestSyncByUuid(UUID uuid) {
        syncRequestedUuids.add(uuid);
    }

    @SuppressWarnings("unused")
    @Mod.Instance
    private static SimpleEconomy instance;

    public static void openGui(EntityPlayer player, int guiId, World world, int x, int y, int z) {
        player.openGui(instance, guiId, world, x, y, z);
    }
}
