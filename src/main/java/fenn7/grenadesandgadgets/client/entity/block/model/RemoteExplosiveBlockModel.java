package fenn7.grenadesandgadgets.client.entity.block.model;

import fenn7.grenadesandgadgets.commonside.GrenadesMod;
import fenn7.grenadesandgadgets.commonside.block.custom.HiddenExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.block.custom.RemoteExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.block.entity.HiddenExplosiveBlockEntity;
import fenn7.grenadesandgadgets.commonside.block.entity.RemoteExplosiveBlockEntity;
import net.minecraft.util.Identifier;

public class RemoteExplosiveBlockModel extends SimpleBlockModel<RemoteExplosiveBlockEntity> {
    private static final String NAME = "remote_explosive_block";
    private static final String TEXTURE = "textures/block/remote_explosive_block_armed.png";
    private static final int UNLIT_ARMED_TICKS = 13;
    private static final int TPS = 20;

    public RemoteExplosiveBlockModel() {
        super(NAME);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public Identifier getTextureLocation(RemoteExplosiveBlockEntity entity) {
        return entity.getCachedState().get(RemoteExplosiveBlock.ARMED)
            && (entity.getWorld().getTime() % TPS > UNLIT_ARMED_TICKS)
            ? new Identifier(GrenadesMod.MOD_ID, TEXTURE) : super.getTextureLocation(entity);
    }
}
