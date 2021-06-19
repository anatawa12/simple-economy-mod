package com.anatawa12.simpleEconomy;

import com.anatawa12.simpleEconomy.gui.GuiHandler;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static com.anatawa12.simpleEconomy.SimpleEconomy.MONEY_LOGGER;

public final class CashBoxBlock extends BlockContainer {
    public static CashBoxBlock INSTANCE = new CashBoxBlock();

    @SuppressWarnings("unused")
    private CashBoxBlock() {
        super(Material.iron);
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
        // can't break with explode
    }

    @Nullable
    private CashBoxTileEntity getTEAndCheckPermission(World world, EntityPlayer player, int x, int y, int z) {
        CashBoxTileEntity tileEntity = CashBoxTileEntity.getOrSet(world, x, y, z);
        if (tileEntity.checkPermission(player)) return tileEntity;

        if (Utils.hasPrivileges(player)) {
            tileEntity.startPrivilege(player);
            player.addChatComponentMessage(new ChatComponentTranslation("block.crash-box.privilege-check"));
        } else {
            player.addChatComponentMessage(new ChatComponentTranslation("block.crash-box.no-permission"));
        }

        return null;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        CashBoxTileEntity tileEntity = getTEAndCheckPermission(world, player, x, y, z);
        if (tileEntity == null) return true;

        SimpleEconomy.openGui(player, GuiHandler.cashBoxGui, world, x, y, z);

        return true;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (world.isRemote) return true;
        CashBoxTileEntity tileEntity = getTEAndCheckPermission(world, player, x, y, z);
        if (tileEntity == null) return true;
        MONEY_LOGGER.info("removed cash box by {} in dimension #{} at ({}, {}, {})",
                player, world.provider.dimensionId, x, y, z);
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack item) {
        if (world.isRemote) return;
        CashBoxTileEntity tileEntity = CashBoxTileEntity.getOrSet(world, x, y, z);
        MONEY_LOGGER.info("placed cash box by {} in dimension #{} at ({}, {}, {})",
                placer, world.provider.dimensionId, x, y, z);
        if (placer instanceof EntityPlayer) {
            tileEntity.addAllowed((EntityPlayer) placer, "placer");
        }

        super.onBlockPlacedBy(world, x, y, z, placer, item);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        if (world.isRemote) return null;
        return new CashBoxTileEntity();
    }
}
