package com.anatawa12.simpleEconomy.gui;

import com.anatawa12.simpleEconomy.SimpleEconomy;
import com.anatawa12.simpleEconomy.Utils;
import com.anatawa12.simpleEconomy.network.MoveCacheWithBox;
import com.anatawa12.simpleEconomy.network.NetworkHandler;
import com.anatawa12.simpleEconomy.network.RemoveAllowedPlayer;
import com.anatawa12.simpleEconomy.network.RequestStartAddingAllowedPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class CashBoxGui extends GuiContainer {
    public BigDecimal cashBoxMoney = BigDecimal.ZERO;
    public List<Pair<UUID, String>> allowed;

    private final World world;
    private final int x;
    private final int y;
    private final int z;

    // status props
    private boolean sendToPlayer;
    // current remove candidate 
    private int currentId = -1;
    private int errorCounter = 0;
    private String errorMessage = "";

    private final GuiTextField sendMoney;
    private final GuiButton addAllowedPlayer;
    private final GuiButton sendDirection;
    private final GuiButton doSend;
    private final GuiButton doRemove;

    private static final int addAllowedPlayerId = 0;
    private static final int sendDirectionId = 1;
    private static final int doSendId = 2;
    private static final int doRemoveId = 3;
    private static final int allowedPlayerBaseId = 16;

    private int textFieldXPosGap = 117;
    private String approvedStr = "0123456789";

    public CashBoxGui(World world, int x, int y, int z) {
        super(new CashBoxContainer());

        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;

        this.xSize = 230;
        this.ySize = 219;
        fontRendererObj = Minecraft.getMinecraft().fontRenderer;
        sendMoney = new GuiTextField(fontRendererObj, 0, 0, 76, 20);
        buttonList().add(addAllowedPlayer = new GuiButton(addAllowedPlayerId, 0, 0, 20, 20, "+"));
        buttonList().add(sendDirection = new GuiButton(sendDirectionId, 0, 0, 20, 20, ""));
        buttonList().add(doSend = new GuiButton(doSendId, 0, 0, 40, 20, "send!"));
        buttonList().add(doRemove = new GuiButton(doRemoveId, 0, 0, 40, 20, "disallow"));
        doRemove.visible = false;
        buttonList = new NonClearableList<>(buttonList());

        SimpleEconomy.Unit unit = SimpleEconomy.getUnit();
        if (unit.isBefore) {
            textFieldXPosGap += fontRendererObj.getStringWidth(unit.unitStr) + 3;
        }
        if (unit.isDecimal) {
            approvedStr += ".";
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        sendMoney.xPosition = guiLeft + textFieldXPosGap;
        sendMoney.yPosition = guiTop + 90;
        addAllowedPlayer.xPosition = guiLeft + 12;
        addAllowedPlayer.yPosition = guiTop + 33;
        sendDirection.xPosition = guiLeft + 153;
        sendDirection.yPosition = guiTop + 65;
        doSend.xPosition = guiLeft + 117;
        doSend.yPosition = guiTop + 136;

        fitPositionOfAllowedPlayerList();
    }

    private void fitPositionOfAllowedPlayerList() {
        for (GuiButton guiButton : buttonList()) {
            if (guiButton.id >= allowedPlayerBaseId) {
                guiButton.xPosition = guiLeft + 12;
                guiButton.yPosition = guiTop + 53 + (guiButton.id - allowedPlayerBaseId) * 20;
            }
        }
        if (currentId != -1) {
            doRemove.xPosition = guiLeft + 12 - 60;
            doRemove.yPosition = guiTop + 53 + currentId * 20;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        sendMoney.updateCursorCounter();
        if (errorCounter > 0) {
            if (--errorCounter == 0)
                errorMessage = "";
        }
    }

    @Override
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        sendMoney.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    @Override
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
        if (!ChatAllowedCharacters.isAllowedCharacter(p_73869_1_) || (approvedStr.indexOf(p_73869_1_) != -1 && !sendMoney.isFocused()))
            super.keyTyped(p_73869_1_, p_73869_2_);
        if (!ChatAllowedCharacters.isAllowedCharacter(p_73869_1_) || approvedStr.indexOf(p_73869_1_) != -1)
            sendMoney.textboxKeyTyped(p_73869_1_, p_73869_2_);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case addAllowedPlayerId:
                SimpleEconomy.openGui(Minecraft.getMinecraft().thePlayer, GuiHandler.cashBoxAddAllowGui, world, x, y, z);
                NetworkHandler.sendToServer(new RequestStartAddingAllowedPlayer());
                return;
            case sendDirectionId:
                sendToPlayer = !sendToPlayer;
                return;
            case doSendId:
                String str = sendMoney.getText();
                if (str.isEmpty()) {
                    emptyError();
                    return;
                }
                BigDecimal bdMoney;
                try {
                    bdMoney = Utils.parseBigDecimalWithUnit(str);
                } catch (NumberFormatException e) {
                    notSuitableError();
                    return;
                }
                if (sendToPlayer) {
                    NetworkHandler.sendToServer(new MoveCacheWithBox(bdMoney.multiply(BigDecimal.valueOf(-1))));
                } else {
                    NetworkHandler.sendToServer(new MoveCacheWithBox(bdMoney));
                }
                return;
            case doRemoveId:
                if (currentId != -1) {
                    NetworkHandler.sendToServer(new RemoveAllowedPlayer(allowed.get(currentId).getLeft()));
                }
                return;
        }
        if (button.id >= allowedPlayerBaseId) {
            if (currentId == -1) {
                currentId = button.id - allowedPlayerBaseId;
                doRemove.visible = true;
                fitPositionOfAllowedPlayerList();
            } else if (currentId == button.id - allowedPlayerBaseId) {
                currentId = -1;
                doRemove.visible = false;
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        fontRendererObj.drawString(I18n.format("block.crash-box.gui.allowed-players"),
                12, 18, 0xFFFFFF);

        fontRendererObj.drawString(I18n.format("block.crash-box.gui.you-have"),
                117, 47, 0xFFFFFF);
        fontRendererObj.drawString(I18n.format("text.general.simple-economy.amount." + SimpleEconomy.getUnit().isBefore + ".%s.%s",
                        SimpleEconomy.clientUsersMoney, SimpleEconomy.getUnit().unitStr),
                117, 55, 0xFFFFFF);
        fontRendererObj.drawString(I18n.format("block.crash-box.gui.box-have"),
                117, 118, 0xFFFFFF);
        fontRendererObj.drawString(I18n.format("text.general.simple-economy.amount." + SimpleEconomy.getUnit().isBefore + ".%s.%s",
                        cashBoxMoney, SimpleEconomy.getUnit().unitStr),
                117, 126, 0xFFFFFF);

        fontRendererObj.drawString(SimpleEconomy.getUnit().unitStr,
                SimpleEconomy.getUnit().isBefore ? 117 : 196, 95, 0xFFFFFF);

        drawCenteredString(fontRendererObj, errorMessage, 135, 160, 0xFFFFFF);

        drawDirectionButton();
    }

    private void drawDirectionButton() {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        // button at 153, 65
        GL11.glTranslatef(153 + 10, 65 + 10, 0);
        GL11.glColor4f(1, 1, 1, 1);
        if (sendToPlayer) {
            GL11.glRotated(180, 0, 0, 1);
        }
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(+1, -7);
        GL11.glVertex2f(-1, -7);
        GL11.glVertex2f(-1, +1);
        GL11.glVertex2f(+1, +1);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2f(-6, +1);
        GL11.glVertex2f(+0, +7);
        GL11.glVertex2f(+6, +1);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(0, 0, 0, 1);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        sendMoney.drawTextBox();
    }

    @SuppressWarnings("unchecked")
    private List<GuiButton> buttonList() {
        return buttonList;
    }

    public void setCashBoxInfo(BigDecimal money, List<Pair<UUID, String>> allowed) {
        this.cashBoxMoney = money;
        allowed.sort(Comparator.comparing(Pair::getLeft));
        if (!allowed.equals(this.allowed)) {
            this.allowed = allowed;
            buttonList().removeIf(x -> x.id >= allowedPlayerBaseId);
            for (int i = 0; i < allowed.size(); i++) {
                buttonList().add(new GuiButton(i + allowedPlayerBaseId, 0, 0, 76, 20, allowed.get(i).getRight()));
            }
            currentId = 0;
            doRemove.visible = false;
            fitPositionOfAllowedPlayerList();
        }
    }

    public void onNoMuchMoneyError() {
        if (sendToPlayer) {
            errorMessage = I18n.format("block.crash-box.gui.cash-box.no-much-money");
        } else {
            errorMessage = I18n.format("block.crash-box.gui.player.no-much-money");
        }
        errorCounter = 5 * 20;
    }

    private void notSuitableError() {
        errorMessage = I18n.format("block.crash-box.gui.input.illegal-value");
    }

    private void emptyError() {
        errorMessage = I18n.format("block.crash-box.gui.input.empty");
    }
}
