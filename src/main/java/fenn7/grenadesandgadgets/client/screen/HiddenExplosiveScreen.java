package fenn7.grenadesandgadgets.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.item.network.GrenadesModC2SPackets;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HiddenExplosiveScreen extends HandledScreen<HiddenExplosiveScreenHandler> {
    private static final Identifier SCREEN = new Identifier(GrenadesMod.MOD_ID, "textures/gui/hidden_explosive_block_gui.png");
    private static final String LEFT_TITLE = "container.grenadesandgadgets.range";
    private static final String RIGHT_TITLE = "container.grenadesandgadgets.arming";
    private static final String CANT_ARM = "container.grenadesandgadgets.cannot_arm";
    private static final String ARM_START = "container.grenadesandgadgets.arming_started";

    public HiddenExplosiveScreen(HiddenExplosiveScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
        this.addSelectableChild(new ButtonWidget(this.x + 124, this.y + 63, 14, 9, GrenadesModUtil.textOf(""),
            widget -> {
                this.handler.setDelegateValue(1, this.handler.hasGrenade() && !this.handler.isArming() ? 1 : 0);
                ClientPlayNetworking.send(GrenadesModC2SPackets.SYNC_HIDDEN_EXPLOSIVE_C2S, GrenadesModUtil.createBuffer().writeBlockPos(this.handler.getBlockEntityPos()));
            })
        );
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SCREEN);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (this.handler.hasGrenade()) {
            this.drawTexture(matrices, this.x + 124, this.y + 63, 176, 63, 14, 9);
        }
        GrenadesMod.LOGGER.warn("SCREEN THINKS ARM FLAG IS " + (this.handler.isArming() ? 1 : 0));
        this.drawTexture(matrices, this.x + 101, this.y + 62, 190, 62, this.handler.getScaledProgress() / 2, 11);
        this.drawTexture(matrices, this.x + 161 - (handler.getScaledProgress() / 2), this.y + 62, 250 - (handler.getScaledProgress() / 2), 62, this.handler.getScaledProgress() / 2, 11);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
        this.textRenderer.draw(matrices, GrenadesModUtil.translatableTextOf(LEFT_TITLE), this.x + 10 + 2, this.y + 20, 0);
        this.textRenderer.draw(matrices, GrenadesModUtil.translatableTextOf(RIGHT_TITLE), this.x + 10 + 88, this.y + 20, 0);
        this.textRenderer.draw(matrices, Text.of(this.handler.getBlockEntityPos().toShortString()), 0, 0, 0);
    }
}
