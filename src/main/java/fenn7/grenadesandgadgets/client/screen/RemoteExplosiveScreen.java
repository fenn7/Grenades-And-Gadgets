package fenn7.grenadesandgadgets.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.item.network.GrenadesModC2SPackets;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RemoteExplosiveScreen extends HandledScreen<RemoteExplosiveScreenHandler> {
    private static final Identifier SCREEN = new Identifier(GrenadesMod.MOD_ID, "textures/gui/remote_explosive_block_gui.png");
    private static final String TIME_TITLE = "container.grenadesandgadgets.time_ticks";

    public RemoteExplosiveScreen(RemoteExplosiveScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    private void setAndSyncValue(int id, int value) {
        this.handler.setDelegateValue(id, value);
        ClientPlayNetworking.send(GrenadesModC2SPackets.SYNC_HIDDEN_EXPLOSIVE_C2S,
            GrenadesModUtil.createBuffer().writeBlockPos(this.handler.getBlockEntityPos()).writeIntArray(new int[]{id, value}));
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SCREEN);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
        Text titleText = GrenadesModUtil.translatableTextOf(TIME_TITLE);
        this.textRenderer.draw(matrices, titleText, this.x + ((this.backgroundWidth - this.textRenderer.getWidth(titleText)) / 2.0F), this.y + 50, 0xBAAAAAAA);
    }
}
