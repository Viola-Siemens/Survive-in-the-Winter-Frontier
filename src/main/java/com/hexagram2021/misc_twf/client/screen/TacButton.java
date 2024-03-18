package com.hexagram2021.misc_twf.client.screen;

import com.hexagram2021.misc_twf.SurviveInTheWinterFrontier;
import com.hexagram2021.misc_twf.common.network.ServerboundOpenTacBackpackPacket;
import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.buttons.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

public class TacButton extends Button {
	private static final ResourceLocation EXTRAS_TAC_TRAVELERS_BACKPACK = new ResourceLocation(MODID, "textures/gui/container/travelers_backpack_tac.png");

	public TacButton(TravelersBackpackScreen screen) {
		super(screen, 16, 73 + screen.container.getYOffset(), 18, 18);
	}

	@Override
	public void render(PoseStack transform, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.setShaderTexture(0, EXTRAS_TAC_TRAVELERS_BACKPACK);
		if(this.screen.container instanceof IAmmoBackpack ammoBackpack && ammoBackpack.canStoreAmmo()) {
			if(this.inButton(mouseX, mouseY)) {
				this.screen.blit(transform, this.screen.getGuiLeft() + this.x, this.screen.getGuiTop() + this.y, 19, 0, this.width, this.height);
			} else {
				this.screen.blit(transform, this.screen.getGuiLeft() + this.x, this.screen.getGuiTop() + this.y, 0, 0, this.width, this.height);
			}
		}
	}

	@Override
	public void renderTooltip(PoseStack transform, int mouseX, int mouseY) {
		if(this.screen.container instanceof IAmmoBackpack ammoBackpack && ammoBackpack.canStoreAmmo()) {
			if(this.inButton(mouseX, mouseY)) {
				this.screen.renderTooltip(transform, new TranslatableComponent("screen.travelersbackpack.tac_button"), mouseX, mouseY);
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(this.screen.container instanceof IAmmoBackpack ammoBackpack && ammoBackpack.canStoreAmmo()) {
			if (this.inButton((int)mouseX, (int)mouseY)) {
				byte screenID = this.screen.container.getScreenID();
				if(this.screen.container instanceof TravelersBackpackBlockEntity blockEntity) {
					SurviveInTheWinterFrontier.packetHandler.sendToServer(new ServerboundOpenTacBackpackPacket(
							ServerboundOpenTacBackpackPacket.TYPE_BACKPACK_TO_TAC_SLOT, screenID, blockEntity.getBlockPos()
					));
				} else {
					SurviveInTheWinterFrontier.packetHandler.sendToServer(new ServerboundOpenTacBackpackPacket(
							ServerboundOpenTacBackpackPacket.TYPE_BACKPACK_TO_TAC_SLOT, screenID
					));
				}
				return true;
			}
		}
		return false;
	}
}
