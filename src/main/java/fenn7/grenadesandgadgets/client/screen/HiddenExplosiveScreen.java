package fenn7.grenadesandgadgets.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HiddenExplosiveScreen extends HandledScreen<HiddenExplosiveScreenHandler> {
    private static final Identifier SCREEN = new Identifier(GrenadesMod.MOD_ID, "textures/gui/hidden_explosive_block_gui.png");
    private static final String LEFT_TITLE = "container.grenadesandgadgets.range";
    private static final String RIGHT_TITLE = "container.grenadesandgadgets.arming";

    public HiddenExplosiveScreen(HiddenExplosiveScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SCREEN);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (this.handler.isCurrentlyArming()) {
            this.drawTexture(matrices, x + 101, y + 62, 190, 62, this.handler.getScaledProgress(), 11);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
        this.textRenderer.draw(matrices, GrenadesModUtil.translatableTextOf(RIGHT_TITLE), this.x + 8 + 88, this.y + 20, 0);
        this.textRenderer.draw(matrices, GrenadesModUtil.translatableTextOf(LEFT_TITLE), this.x + 8, this.y + 20, 0);
    }
}
