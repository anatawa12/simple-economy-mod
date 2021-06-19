package com.anatawa12.simpleEconomy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class MoneyManager extends WorldSavedData {
    public static String identifier = "com.anatawa12.simpleEconomy.MoneyManager";
    private final Map<String, Player> playerByName = new HashMap<>();
    private final Map<UUID, Player> playerByUUID = new HashMap<>();

    @SuppressWarnings("unused")
    public MoneyManager(String identifier) {
        super(identifier);
    }

    public MoneyManager() {
        super(identifier);
    }

    @Nullable
    public static Player getPlayerByName(String playerName) {
        return getInstance().getPlayerByNameInternal(playerName);
    }

    @Nonnull
    public static Player getPlayerByEntity(EntityPlayer entityPlayer) {
        return getInstance().getPlayerByEntityInternal(entityPlayer);
    }

    @Nonnull
    public static Player getPlayerByUUID(UUID uuid) {
        return getInstance().getPlayerByUUIDInternal(uuid);
    }

    @SuppressWarnings("unchecked")
    public static List<Pair<UUID, String>> getPlayersInfo() {
        // ensure logged-in players are listed up
        for (EntityPlayerMP playerMP : (List<EntityPlayerMP>) MinecraftServer.getServer()
                .getConfigurationManager().playerEntityList) {
            getPlayerByEntity(playerMP);
        }

        return getInstance().playerByUUID.values().stream()
                .filter(x -> x.getName() != null)
                .map(x -> Pair.of(x.uuid, Utils.toStringForGui(x)))
                .collect(Collectors.toList());
    }

    @Nullable
    private Player getPlayerByNameInternal(@Nonnull String playerName) {
        EntityPlayerMP entityPlayerMP = MinecraftServer.getServer().getConfigurationManager().func_152612_a(playerName);

        if (entityPlayerMP != null) {
            return getPlayerByEntityInternal(entityPlayerMP);
        } else {
            return playerByName.get(playerName);
        }
    }

    @Nonnull
    private Player getPlayerByEntityInternal(@Nonnull EntityPlayer entityPlayer) {
        if (entityPlayer.worldObj.isRemote)
            throw new IllegalStateException("supported only on server");

        UUID uuid = entityPlayer.getUniqueID();
        String playerName = entityPlayer.getDisplayName();

        Player player = playerByUUID.get(uuid);

        if (player == null) player = new Player(uuid);
        player.updateName(playerName);
        return player;
    }

    @Nonnull
    private Player getPlayerByUUIDInternal(UUID uuid) {
        EntityPlayerMP playerMP = Utils.findPlayer(uuid);
        Player player = playerByUUID.get(uuid);

        if (player == null) player = new Player(uuid);

        if (playerMP != null) player.updateName(playerMP.getCommandSenderName());

        return player;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        boolean dirtyOld = isDirty();

        NBTTagList tags = compound.getTagList("l", 10);

        for (int i = 0; i < tags.tagCount(); i++) {
            NBTTagCompound playerCompound = tags.getCompoundTagAt(i);
            Player player = new Player(new UUID(
                    playerCompound.getLong("uuidM"),
                    playerCompound.getLong("uuidL")));
            player.updateName(playerCompound.getString("nane"));
            player.money = playerCompound.getLong("money");
        }

        setDirty(dirtyOld);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList tags = new NBTTagList();

        for (Player player : playerByName.values()) {
            NBTTagCompound playerCompound = new NBTTagCompound();
            playerCompound.setLong("uuidM", player.uuid.getMostSignificantBits());
            playerCompound.setLong("uuidL", player.uuid.getLeastSignificantBits());
            playerCompound.setString("name", player.name);
            playerCompound.setLong("money", player.money);
            tags.appendTag(playerCompound);
        }

        compound.setTag("l", tags);
    }

    public class Player {
        private @Nullable String name;
        private @Nonnull final UUID uuid;
        private long money;

        private Player(@Nonnull UUID uuid) {
            if (playerByUUID.containsKey(uuid))
                throw new IllegalStateException("uuid duplicate");
            this.uuid = uuid;

            markDirty();
            playerByUUID.put(uuid, this);
        }

        private void updateName(@Nullable String name) {
            markDirty();
            if (this.name != null) {
                Player old = playerByName.remove(this.name.toLowerCase(Locale.ROOT));
                if (old != null) old.name = null;
            }
            if (name == null) {
                this.name = null;
                return;
            }
            this.name = name;
            playerByName.put(name.toLowerCase(Locale.ROOT), this);
        }

        @Override
        public String toString() {
            if (name == null)
                return "(no name:" + uuid + ")";
            return name;
        }

        @Nullable
        public String getName() {
            return name;
        }

        public UUID getUUID() {
            return uuid;
        }

        public long getMoney() {
            return money;
        }

        public void addMoney(long difference) {
            markDirty();
            this.money += difference;
            SimpleEconomy.requestSyncByUuid(uuid);
        }
    }

    // utils

    @Nonnull
    private static MoneyManager getInstance() {
        World world = MinecraftServer.getServer().getEntityWorld();
        if (world.isRemote)
            throw new IllegalStateException("client side");
        MoneyManager money = (MoneyManager)world.loadItemData(MoneyManager.class, identifier);
        if (money == null) {
            money = new MoneyManager();
            world.setItemData(identifier, money);
        }
        return money;
    }
}
