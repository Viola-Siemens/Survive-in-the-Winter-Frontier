package com.hexagram2021.misc_twf.client.screen;

import com.hexagram2021.misc_twf.common.menu.UltravioletLampMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

@OnlyIn(Dist.CLIENT)
public class UltravioletLampScreen extends AbstractContainerScreen<UltravioletLampMenu> {
	private static final ResourceLocation BG_LOCATION = new ResourceLocation(MODID, "textures/gui/container/ultraviolet_lamp.png");

	public UltravioletLampScreen(UltravioletLampMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	public void render(PoseStack transform, int x, int y, float partialTicks) {
		super.render(transform, x, y, partialTicks);
		this.renderTooltip(transform, x, y);
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, BG_LOCATION);
		int left = this.leftPos;
		int top = this.topPos;
		this.blit(transform, left, top, 0, 0, this.imageWidth, this.imageWidth);
	}
}
