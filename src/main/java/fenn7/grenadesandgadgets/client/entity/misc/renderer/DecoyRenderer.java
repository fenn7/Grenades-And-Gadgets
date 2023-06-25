package fenn7.grenadesandgadgets.client.entity.misc.renderer;

import fenn7.grenadesandgadgets.client.entity.misc.model.DecoyModel;
import fenn7.grenadesandgadgets.commonside.entity.misc.DecoyEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import software.bernie.example.client.EntityResources;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class DecoyRenderer extends ExtendedGeoEntityRenderer<DecoyEntity> {
    // TODO: EXTEND TO WORK WITH ALL ARMOUR AND ITEM SLOTS. THEN REWORK CLASS VARIABLES AS A MAP
    protected ItemStack mainHandItem;
    protected ItemStack offHandItem;
    protected ItemStack helmetItem;
    protected ItemStack chestplateItem;
    protected ItemStack leggingsItem;
    protected ItemStack bootsItem;
    
    public DecoyRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new DecoyModel());
    }

    @Override
    public void renderEarly(DecoyEntity animatable, MatrixStack poseStack, float partialTick, VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, partialTicks);
        Entity nbtEntity = DecoyModel.getEntityInDecoyNbt(animatable);
        if (nbtEntity instanceof ClientPlayerEntity cPlayer) {
            this.mainHandItem = cPlayer.getEquippedStack(EquipmentSlot.MAINHAND);
            this.offHandItem = cPlayer.getEquippedStack(EquipmentSlot.OFFHAND);
            this.helmetItem = cPlayer.getEquippedStack(EquipmentSlot.HEAD);
            this.chestplateItem = cPlayer.getEquippedStack(EquipmentSlot.CHEST);
            this.leggingsItem = cPlayer.getEquippedStack(EquipmentSlot.LEGS);
            this.bootsItem = cPlayer.getEquippedStack(EquipmentSlot.FEET);
        }
    }

    public void render(DecoyEntity animatable, float yaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
        Entity nbtEntity = DecoyModel.getEntityInDecoyNbt(animatable);
        if (nbtEntity instanceof ClientPlayerEntity cPlayer) {
        }
    }

    protected ItemStack getHeldItemForBone(String boneName, DecoyEntity currentEntity) {
        return switch(boneName) {
            case "bipedHandLeft" -> this.offHandItem;
            case "bipedHandRight" -> this.mainHandItem;
            default -> null;
        };
    }

    protected ModelTransformation.Mode getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
        return switch(boneName) {
            case "bipedHandLeft", "bipedHandRight" -> ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND;
            default -> ModelTransformation.Mode.NONE;
        };
    }

    protected void preRenderItem(MatrixStack stack, ItemStack item, String boneName, DecoyEntity currentEntity, IBone bone) {
        if (item == this.mainHandItem) {
            stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            if (item.getItem() instanceof ShieldItem) {
                stack.translate(0.0, 0.125, -0.25);
            }
        } else if (item == this.offHandItem) {
            stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            if (item.getItem() instanceof ShieldItem) {
                stack.translate(0.0, 0.125, 0.25);
                stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            }
        }
    }

    protected void postRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, DecoyEntity currentEntity, IBone bone) {
    }

    protected ItemStack getArmorForBone(String boneName, DecoyEntity currentEntity) {
        return switch(boneName) {
            case "armorBipedLeftFoot", "armorBipedRightFoot", "armorBipedLeftFoot2", "armorBipedRightFoot2" -> this.bootsItem;
            case "armorBipedLeftLeg", "armorBipedRightLeg", "armorBipedLeftLeg2", "armorBipedRightLeg2" -> this.leggingsItem;
            case "armorBipedBody", "armorBipedRightArm", "armorBipedLeftArm" -> this.chestplateItem;
            case "armorHead" -> this.helmetItem;
            default -> null;
        };
    }

    protected EquipmentSlot getEquipmentSlotForArmorBone(String boneName, DecoyEntity currentEntity) {
        return switch(boneName) {
            case "armorBipedLeftFoot", "armorBipedRightFoot", "armorBipedLeftFoot2", "armorBipedRightFoot2" -> EquipmentSlot.FEET;
            case "armorBipedLeftLeg", "armorBipedRightLeg", "armorBipedLeftLeg2", "armorBipedRightLeg2" -> EquipmentSlot.LEGS;
            case "armorBipedRightArm" -> EquipmentSlot.MAINHAND;
            case "armorBipedLeftArm" -> EquipmentSlot.OFFHAND;
            case "armorBipedBody" -> EquipmentSlot.CHEST;
            case "armorHead" -> EquipmentSlot.HEAD;
            default -> null;
        };
    }

    protected ModelPart getArmorPartForBone(String name, BipedEntityModel<?> armorModel) {
        return switch(name) {
            case "armorBipedLeftFoot", "armorBipedLeftLeg", "armorBipedLeftFoot2", "armorBipedLeftLeg2" -> armorModel.leftLeg;
            case "armorBipedRightFoot", "armorBipedRightLeg", "armorBipedRightFoot2", "armorBipedRightLeg2" -> armorModel.rightLeg;
            case "armorBipedRightArm" -> armorModel.rightArm;
            case "armorBipedLeftArm" -> armorModel.leftArm;
            case "armorBipedBody" -> armorModel.body;
            case "armorHead" -> armorModel.head;
            default -> null;
        };
    }

    protected BlockState getHeldBlockForBone(String boneName, DecoyEntity currentEntity) {
        return null;
    }

    protected void preRenderBlock(MatrixStack stack, BlockState block, String boneName, DecoyEntity currentEntity) {
    }

    protected void postRenderBlock(MatrixStack stack, BlockState block, String boneName, DecoyEntity currentEntity) {
    }

    protected Identifier getTextureForBone(String boneName, DecoyEntity animatable) {
        return "bipedCape".equals(boneName) ? EntityResources.EXTENDED_CAPE_TEXTURE : null;
    }

    protected boolean isArmorBone(GeoBone bone) {
        return bone.getName().startsWith("armor");
    }
}
