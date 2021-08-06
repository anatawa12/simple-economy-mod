package com.anatawa12.simpleEconomy;

import com.anatawa12.simpleEconomy.gui.CashBoxContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.anatawa12.simpleEconomy.SimpleEconomy.MONEY_LOGGER;

public final class CashBoxTileEntity extends TileEntity {
    private final CashBoxContainer container = new CashBoxContainer(this);
    private final List<PrivilegeCheck> privilegeChecks = new ArrayList<>();
    private final Set<UUID> allowedList = new HashSet<>();
    private BigDecimal money = BigDecimal.ZERO;

    @Nullable
    public static CashBoxTileEntity getOrSetChecked(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z) != CashBoxBlock.INSTANCE) return null;
        return getOrSet(world, x, y, z);
    }

    @Nonnull
    public static CashBoxTileEntity getOrSet(World world, int x, int y, int z) {
        TileEntity entity = world.getTileEntity(x, y, z);
        if (entity instanceof CashBoxTileEntity) return (CashBoxTileEntity) entity;
        CashBoxTileEntity newEntity = new CashBoxTileEntity();
        world.setTileEntity(x, y, z, newEntity);
        return newEntity;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        updatePrivilegeChecks();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        {
            allowedList.clear();
            NBTTagList allowedList = compound.getTagList("allowedList", 10);
            for (int i = 0; i < allowedList.tagCount(); i++) {
                this.allowedList.add(Utils.decodeUuid(allowedList.getCompoundTagAt(i)));
            }
        }

        money = Utils.parseBigDecimalWithUnitWithoutError(compound.getString("money"));
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        {
            NBTTagList allowedList = new NBTTagList();
            for (UUID s : this.allowedList)
                allowedList.appendTag(Utils.encodeUuid(s));
            compound.setTag("allowedList", allowedList);
        }

        compound.setString("money", money.toString());
    }

    private void updatePrivilegeChecks() {
        privilegeChecks.removeIf(privilegeCheck -> privilegeCheck.remain-- < 0);
    }

    public void addAllowed(EntityPlayer player, Object user) {
        allowedList.add(player.getUniqueID());
        MONEY_LOGGER.info("added {} by {} to allow list of cash box in dimension #{} at ({}, {}, {})",
                player, user, worldObj.provider.dimensionId, xCoord, yCoord, zCoord);
        getContainer().sendUpdate();
    }

    public void addAllowed(UUID uuid, Object user) {
        allowedList.add(uuid);
        MONEY_LOGGER.info("added {} by {} to allow list of cash box in dimension #{} at ({}, {}, {})",
                MoneyManager.getPlayerByUUID(uuid), user, worldObj.provider.dimensionId, xCoord, yCoord, zCoord);
        getContainer().sendUpdate();
    }

    public void removeAllowed(UUID uuid, Object user) {
        allowedList.remove(uuid);
        MONEY_LOGGER.info("removed {} by {} to allow list of cash box in dimension #{} at ({}, {}, {})",
                MoneyManager.getPlayerByUUID(uuid), user, worldObj.provider.dimensionId, xCoord, yCoord, zCoord);
        getContainer().sendUpdate();
    }

    public boolean checkAllowedOrOp(EntityPlayer player) {
        if (checkAllowed(player)) return true;
        if (Utils.hasPrivileges(player)) return true;
        return false;
    }

    public boolean checkAllowed(EntityPlayer player) {
        return allowedList.contains(player.getUniqueID());
    }

    public boolean checkPermission(EntityPlayer player) {
        UUID uuid = player.getUniqueID();
        if (allowedList.contains(uuid)) return true;
        if (Utils.hasPrivileges(player)) {
            for (PrivilegeCheck privilegeCheck : privilegeChecks) {
                if (privilegeCheck.playerUuid.equals(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void startPrivilege(EntityPlayer player) {
        privilegeChecks.add(new PrivilegeCheck(player.getUniqueID()));
    }

    @Nonnull
    public CashBoxContainer getContainer() {
        return container;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public List<Pair<UUID, String>> getAllowedPlayersInfo() {
        return allowedList.stream()
                .map(x -> Pair.of(x, Utils.toStringForGui(MoneyManager.getPlayerByUUID(x))))
                .collect(Collectors.toList());
    }

    public boolean moveMoney(EntityPlayerMP from, BigDecimal mount) {
        MoneyManager.Player moneyPlayer = MoneyManager.getPlayerByEntity(from);
        if (mount.compareTo(BigDecimal.ZERO) > 0 && moneyPlayer.getMoney().compareTo(mount) < 0) return false;
        if (mount.compareTo(BigDecimal.ZERO) < 0 && this.money.compareTo(mount.multiply(BigDecimal.valueOf(-1))) < 0)
            return false;
        moneyPlayer.addMoney(mount.multiply(BigDecimal.valueOf(-1)));
        this.money = this.money.add(mount);
        MONEY_LOGGER.info("moved {} from {} to cash box in dimension #{} at ({}, {}, {})",
                mount, from, worldObj.provider.dimensionId, xCoord, yCoord, zCoord);
        getContainer().sendUpdate();
        return true;
    }

    private static class PrivilegeCheck {
        public final UUID playerUuid;
        public int remain;

        public PrivilegeCheck(UUID privilegedPlayerUuid) {
            this.playerUuid = privilegedPlayerUuid;
            // 3 sec
            remain = 3 * 20;
        }
    }
}
