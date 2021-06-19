package com.anatawa12.simpleEconomy;

import com.anatawa12.simpleEconomy.gui.GuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static com.anatawa12.simpleEconomy.SimpleEconomy.MONEY_LOGGER;

public final class CashBoxBlock extends BlockContainer {
    public static CashBoxBlock INSTANCE = new CashBoxBlock();

    @SideOnly(Side.CLIENT)
    private IIcon frontIcon;

    @SuppressWarnings("unused")
    private CashBoxBlock() {
        super(Material.iron);
        setCreativeTab(CreativeTabs.tabDecorations);
        setBlockName("simple-economy-mod:cash-box");
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
        int direction = MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        switch (direction) {
            case 0:
                world.setBlockMetadataWithNotify(x, y, z, 2, 2);
                break;
            case 1:
                world.setBlockMetadataWithNotify(x, y, z, 5, 2);
                break;
            case 2:
                world.setBlockMetadataWithNotify(x, y, z, 3, 2);
                break;
            case 3:
                world.setBlockMetadataWithNotify(x, y, z, 4, 2);
                break;
        }

        if (world.isRemote) return;
        CashBoxTileEntity tileEntity = CashBoxTileEntity.getOrSet(world, x, y, z);
        MONEY_LOGGER.info("placed cash box by {} in dimension #{} at ({}, {}, {})",
                placer, world.provider.dimensionId, x, y, z);
        if (placer instanceof EntityPlayer) {
            tileEntity.addAllowed((EntityPlayer) placer, "placer");
        }

        super.onBlockPlacedBy(world, x, y, z, placer, item);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        // extra: for item rendering
        if (meta == 0 && side == 3) return this.frontIcon;
        if (side != meta) return this.blockIcon;
        return this.frontIcon;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        this.blockIcon = p_149651_1_.registerIcon("iron_block");
        this.frontIcon = p_149651_1_.registerIcon("simple-economy-mod:cash_box_front");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        if (world.isRemote) return null;
        return new CashBoxTileEntity();
    }
}
