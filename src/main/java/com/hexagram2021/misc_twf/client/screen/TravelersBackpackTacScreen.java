package com.hexagram2021.misc_twf.client.screen;

import com.hexagram2021.misc_twf.SurviveInTheWinterFrontier;
import com.hexagram2021.misc_twf.common.menu.AbstractTravelersBackpackTacMenu;
import com.hexagram2021.misc_twf.common.network.ServerboundOpenTacBackpackPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackTacScreen extends AbstractContainerScreen<AbstractTravelersBackpackTacMenu> implements MenuAccess<AbstractTravelersBackpackTacMenu> {
	private static final ResourceLocation EXTRAS_TAC_TRAVELERS_BACKPACK = new ResourceLocation(MODID, "textures/gui/container/travelers_backpack_tac.png");
	private static final ResourceLocation TRAVELERS_BACKPACK_TAC_SLOT = new ResourceLocation(MODID, "textures/gui/container/travelers_backpack_tac_slot.png");

	private static final int BACK_BUTTON_X = 152;
	private static final int BACK_BUTTON_Y = 42;
	private static final int BACK_BUTTON_WIDTH = 18;
	private static final int BACK_BUTTON_HEIGHT = 18;

	private final byte screenId;

	public TravelersBackpackTacScreen(AbstractTravelersBackpackTacMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		this.screenId = ((ITravelersBackpackContainer)menu.container).getScreenID();
		this.imageWidth = 176;
		this.imageHeight = 144;
	}

	@Override
	public void render(PoseStack transform, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(transform);
		super.render(transform, mouseX, mouseY, partialTicks);
		this.renderTooltip(transform, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TRAVELERS_BACKPACK_TAC_SLOT);
		this.blit(transform, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, EXTRAS_TAC_TRAVELERS_BACKPACK);
		if(this.leftPos + BACK_BUTTON_X <= mouseX && mouseX < this.leftPos + BACK_BUTTON_X + BACK_BUTTON_WIDTH &&
				this.topPos + BACK_BUTTON_Y <= mouseY && mouseY < this.topPos + BACK_BUTTON_Y + BACK_BUTTON_HEIGHT) {
			this.blit(transform, this.leftPos + BACK_BUTTON_X, this.topPos + BACK_BUTTON_Y, 19, 19, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT);
		} else {
			this.blit(transform, this.leftPos + BACK_BUTTON_X, this.topPos + BACK_BUTTON_Y, 0, 19, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT);
		}
	}

	@Override
	protected void renderTooltip(PoseStack transform, int mouseX, int mouseY) {
		super.renderTooltip(transform, mouseX, mouseY);

		if(this.leftPos + BACK_BUTTON_X <= mouseX && mouseX < this.leftPos + BACK_BUTTON_X + BACK_BUTTON_WIDTH &&
				this.topPos + BACK_BUTTON_Y <= mouseY && mouseY < this.topPos + BACK_BUTTON_Y + BACK_BUTTON_HEIGHT) {
			this.renderTooltip(transform, new TranslatableComponent("screen.travelersbackpack.tac_back"), mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(this.leftPos + BACK_BUTTON_X <= mouseX && mouseX < this.leftPos + BACK_BUTTON_X + BACK_BUTTON_WIDTH &&
				this.topPos + BACK_BUTTON_Y <= mouseY && mouseY < this.topPos + BACK_BUTTON_Y + BACK_BUTTON_HEIGHT) {
			if(this.menu.container instanceof TravelersBackpackBlockEntity blockEntity) {
				SurviveInTheWinterFrontier.packetHandler.sendToServer(new ServerboundOpenTacBackpackPacket(
						ServerboundOpenTacBackpackPacket.TYPE_TAC_SLOT_TO_BACKPACK, this.screenId, blockEntity.getBlockPos()
				));
			} else {
				SurviveInTheWinterFrontier.packetHandler.sendToServer(new ServerboundOpenTacBackpackPacket(
						ServerboundOpenTacBackpackPacket.TYPE_TAC_SLOT_TO_BACKPACK, this.screenId
				));
			}
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
}
