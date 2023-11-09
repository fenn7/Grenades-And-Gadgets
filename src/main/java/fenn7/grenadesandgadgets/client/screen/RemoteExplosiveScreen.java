package fenn7.grenadesandgadgets.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.block.custom.RemoteExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.block.entity.RemoteExplosiveBlockEntity;
import fenn7.grenadesandgadgets.commonside.item.network.GrenadesModC2SPackets;
import fenn7.grenadesandgadgets.commonside.util.GrenadesModUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class RemoteExplosiveScreen extends HandledScreen<RemoteExplosiveScreenHandler> {
    private static final Identifier SCREEN = new Identifier(GrenadesMod.MOD_ID, "textures/gui/remote_explosive_block_gui.png");
    private static final String TIME_TITLE = "container.grenadesandgadgets.time_ticks";
    private static final String ARMED = "container.grenadesandgadgets.armed_remote";
    private static final int TPS = 20;
    private static final int INCREMENT_BUTTONS = 4;
    private static final int BUTTON_DIMENSION = 14;
    private static final int BUTTON_X_RIGHT = 132;
    private static final int BUTTON_X_LEFT = 16;
    private static final int BUTTON_Y = 29;

    public RemoteExplosiveScreen(RemoteExplosiveScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
        for (int i = 0; i < INCREMENT_BUTTONS; i++) {
            int increment = (int) (1 + Math.pow(i, 2));
            this.addSelectableChild(new ButtonWidget(this.x + BUTTON_X_RIGHT + (i % 2 == 0 ? 0 : BUTTON_DIMENSION),
                this.y + BUTTON_Y + (i / 2 <= 0 ? 0 : BUTTON_DIMENSION), BUTTON_DIMENSION, BUTTON_DIMENSION, Text.of("+" + increment),
                button -> {
                    if (!this.isBlockArmed()) {
                        this.setAndSyncValue(0, MathHelper.clamp(this.handler.getDelegateValue(0) + (increment * TPS), 0, RemoteExplosiveBlockEntity.MAX_DELAY_TICKS));
                    }
                }));
            this.addSelectableChild(new ButtonWidget(this.x + BUTTON_X_LEFT + (i % 2 == 0 ? 0 : BUTTON_DIMENSION),
                this.y + BUTTON_Y + (i / 2 <= 0 ? 0 : BUTTON_DIMENSION), BUTTON_DIMENSION, BUTTON_DIMENSION, Text.of("-" + increment),
                button -> {
                    if (!this.isBlockArmed()) {
                        this.setAndSyncValue(0, MathHelper.clamp(this.handler.getDelegateValue(0) - (increment * TPS), 0, RemoteExplosiveBlockEntity.MAX_DELAY_TICKS));
                    }
                }));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private BlockState getBlockState() {
        var state = MinecraftClient.getInstance().world.getBlockState(this.handler.getBlockEntityPos());
        return MinecraftClient.getInstance().world.getBlockEntity(this.handler.getBlockEntityPos()) instanceof RemoteExplosiveBlockEntity ? state : null;
    }

    private boolean isBlockArmed() {
        var state = this.getBlockState();
        return state != null && state.get(RemoteExplosiveBlock.ARMED);
    }

    private void setAndSyncValue(int id, int value) {
        this.handler.setDelegateValue(id, value);
        ClientPlayNetworking.send(GrenadesModC2SPackets.SYNC_REMOTE_EXPLOSIVE_C2S,
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
        if (this.isBlockArmed()) {
            this.drawTexture(matrices, this.x + 57, this.y + 48, 176, 0, 62, 16);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
        Text titleText = GrenadesModUtil.translatableTextOf(this.isBlockArmed() ? ARMED : TIME_TITLE).append(" " + this.handler.getDelegateValue(0) / TPS + "s");
        this.textRenderer.draw(matrices, titleText, this.x + ((this.backgroundWidth - this.textRenderer.getWidth(titleText)) / 2.0F), this.y + 53, 0xCCCCCCCC);
    }
}
