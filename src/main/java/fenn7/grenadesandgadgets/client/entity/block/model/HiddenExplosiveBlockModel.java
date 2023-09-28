package fenn7.grenadesandgadgets.client.entity.block.model;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.block.custom.HiddenExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.block.entity.HiddenExplosiveBlockEntity;
import net.minecraft.util.Identifier;

public class HiddenExplosiveBlockModel extends SimpleBlockModel<HiddenExplosiveBlockEntity> {
    private static final String NAME = "hidden_explosive_block";
    private static final String TEXTURE = "textures/block/hidden_explosive_block_armed.png";
    private static final int UNLIT_ARMED_TICKS = 13;
    private static final int TPS = 20;

    public HiddenExplosiveBlockModel() {
        super(NAME);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public Identifier getTextureLocation(HiddenExplosiveBlockEntity entity) {
        return entity.getCachedState().get(HiddenExplosiveBlock.ARMED)
            && (entity.getWorld().getTime() % TPS > UNLIT_ARMED_TICKS)
            ? new Identifier(GrenadesMod.MOD_ID, TEXTURE) : super.getTextureLocation(entity);
    }
}
