package com.anatawa12.simpleEconomy.gui;

import com.anatawa12.simpleEconomy.SimpleEconomy;
import com.anatawa12.simpleEconomy.network.AddAllowedPlayer;
import com.anatawa12.simpleEconomy.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class CashBoxAddAllowGui extends GuiContainer {
    public List<Pair<UUID, String>> players;

    private final World world;
    private final int x;
    private final int y;
    private final int z;

    private static final int allowedPlayerBaseId = 16;

    private static final int singleColumnButtonCount = 10;

    public CashBoxAddAllowGui(World world, int x, int y, int z) {
        super(new CashBoxContainer());

        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;

        this.xSize = 230;
        this.ySize = 220;
    }

    @Override
    public void initGui() {
        super.initGui();
        fitPositionOfAllowedPlayerList();
    }

    private void fitPositionOfAllowedPlayerList() {
        for (GuiButton guiButton : buttonList()) {
            if (guiButton.id >= allowedPlayerBaseId) {
                int index = guiButton.id - allowedPlayerBaseId;
                int col = index / singleColumnButtonCount;
                int row = index % singleColumnButtonCount;
                guiButton.xPosition = guiLeft + 1 + col * 76;
                guiButton.yPosition = guiTop + 5 + row * 20;
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        // button.id
        if (button.id >= allowedPlayerBaseId) {
            NetworkHandler.sendToServer(new AddAllowedPlayer(players.get(button.id - allowedPlayerBaseId).getLeft()));
            SimpleEconomy.openGui(Minecraft.getMinecraft().thePlayer, GuiHandler.cashBoxGui, world, x, y, z);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(0.5f, 0.5f, 0.5f, 1);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    @SuppressWarnings("unchecked")
    private List<GuiButton> buttonList() {
        return buttonList;
    }

    public void setPlayerList(List<Pair<UUID, String>> players) {
        players.sort(Comparator.comparing(Pair::getLeft));
        if (!players.equals(this.players)) {
            this.players = players;
            buttonList().removeIf(x -> x.id >= allowedPlayerBaseId);
            for (int i = 0; i < players.size(); i++) {
                buttonList().add(new GuiButton(i + allowedPlayerBaseId, 0, 0, 76, 20, players.get(i).getRight()));
            }
            fitPositionOfAllowedPlayerList();
        }
    }
}
