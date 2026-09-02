package com.hexagram2021.misc_twf.client.screen;

import com.hexagram2021.misc_twf.common.menu.UltravioletLampMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 强紫外线照射灯的客户端 GUI 界面喵~
 * 提供紫外线照射灯的物品操作界面喵~
 *
 * @author liudongyu
 */
@OnlyIn(Dist.CLIENT)
public class UltravioletLampScreen extends AbstractContainerScreen<UltravioletLampMenu> {
	private static final ResourceLocation BG_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/container/ultraviolet_lamp.png");

	/**
	 * 构造函数
	 * @param menu 菜单
	 * @param inventory 玩家物品栏
	 * @param title 标题
	 */
	public UltravioletLampScreen(UltravioletLampMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	public void render(GuiGraphics transform, int x, int y, float partialTicks) {
		super.render(transform, x, y, partialTicks);
		this.renderTooltip(transform, x, y);
	}

	@Override
	protected void renderBg(GuiGraphics transform, float partialTicks, int x, int y) {
		int left = this.leftPos;
		int top = this.topPos;
		transform.blit(BG_LOCATION, left, top, 0, 0, this.imageWidth, this.imageHeight);
	}
}
