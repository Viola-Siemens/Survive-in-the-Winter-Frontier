package com.hexagram2021.misc_twf.mixin;

import com.hexagram2021.misc_twf.SurviveInTheWinterFrontier;
import com.hexagram2021.misc_twf.common.network.ServerboundOpenTacBackpackPacket;
import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.screens.ScreenImageButton;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBaseMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@Mixin(TravelersBackpackScreen.class)
public class TravelersBackpackScreenMixin {
	@Shadow @Final public ITravelersBackpackContainer container;
	private static final ResourceLocation TAC_TRAVELERS_BACKPACK = new ResourceLocation(MODID, "textures/gui/container/travelers_backpack_tac.png");

	@SuppressWarnings("NotNullFieldNotInitialized")
	private ScreenImageButton TAC_BUTTON;

	@Inject(method = "<init>", at = @At(value = "TAIL"), remap = false)
	public void addTACButton(TravelersBackpackBaseMenu screenContainer, Inventory inventory, Component component, CallbackInfo ci) {
		this.TAC_BUTTON = new ScreenImageButton(16, 73 + screenContainer.container.getTier().getMenuSlotPlacementFactor(), 18, 18);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/tiviacz/travelersbackpack/client/screens/widgets/ControlTab;render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V", shift = At.Shift.BEFORE), remap = false)
	public void renderTACButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		RenderSystem.setShaderTexture(0, TAC_TRAVELERS_BACKPACK);
		if(this.container instanceof IAmmoBackpack ammoBackpack && ammoBackpack.canStoreAmmo()) {
			TravelersBackpackScreen current = (TravelersBackpackScreen)(Object)this;
			if(this.TAC_BUTTON.inButton(current, mouseX, mouseY)) {
				this.TAC_BUTTON.draw(poseStack, current, 19, 0);
			} else {
				this.TAC_BUTTON.draw(poseStack, current, 0, 0);
			}
		}
	}

	@Inject(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lcom/tiviacz/travelersbackpack/client/screens/widgets/CraftingWidget;renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;II)V", shift = At.Shift.BEFORE), remap = false)
	public void renderTACButtonTooltip(PoseStack poseStack, int mouseX, int mouseY, CallbackInfo ci) {
		if(this.container instanceof IAmmoBackpack ammoBackpack && ammoBackpack.canStoreAmmo()) {
			TravelersBackpackScreen current = (TravelersBackpackScreen)(Object)this;
			if(this.TAC_BUTTON.inButton(current, mouseX, mouseY)) {
				current.renderTooltip(poseStack, new TranslatableComponent("screen.travelersbackpack.tac_button"), mouseX, mouseY);
			}
		}
	}

	@Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;mouseClicked(DDI)Z", shift = At.Shift.BEFORE, ordinal = 0), remap = false, cancellable = true)
	public void onClickTACButton(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		if(this.container instanceof IAmmoBackpack ammoBackpack && ammoBackpack.canStoreAmmo()) {
			TravelersBackpackScreen current = (TravelersBackpackScreen)(Object)this;
			if (this.TAC_BUTTON.inButton(current, (int)mouseX, (int)mouseY)) {
				SurviveInTheWinterFrontier.packetHandler.sendToServer(new ServerboundOpenTacBackpackPacket());
				cir.setReturnValue(true);
				cir.cancel();
			}
		}
	}
}
