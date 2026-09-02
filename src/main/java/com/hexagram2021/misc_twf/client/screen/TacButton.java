package com.hexagram2021.misc_twf.client.screen;

import com.hexagram2021.misc_twf.common.network.ServerboundOpenTacBackpackPacket;
import com.hexagram2021.misc_twf.common.util.IAmmoBackpack;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.buttons.Button;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.Optional;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 旅行背包界面中的 TAC 弹药槽按钮喵~
 * 当旅行背包已升级为支持弹药存储时，在背包界面中显示此按钮喵~
 * 点击后向服务端发送数据包，切换到 TAC 弹药槽界面喵~
 *
 * @author liudongyu
 */
public class TacButton extends Button {
	private static final ResourceLocation EXTRAS_TAC_TRAVELERS_BACKPACK = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/container/travelers_backpack_tac.png");

	/**
	 * 构造函数
	 * @param screen 背包界面
	 */
	public TacButton(BackpackScreen screen) {
		super(screen, 16, 73 + screen.visibleRows * 18, 18, 18);
	}

	@Override
	public void render(GuiGraphics transform, int mouseX, int mouseY, float partialTicks) {
		if(this.screen.getWrapper() instanceof IAmmoBackpack ammoBackpack && ammoBackpack.canStoreAmmo()) {
			if(this.inButton(mouseX, mouseY)) {
				transform.blit(EXTRAS_TAC_TRAVELERS_BACKPACK, this.screen.getGuiLeft() + this.x, this.screen.getGuiTop() + this.y, 19, 0, this.width, this.height);
			} else {
				transform.blit(EXTRAS_TAC_TRAVELERS_BACKPACK, this.screen.getGuiLeft() + this.x, this.screen.getGuiTop() + this.y, 0, 0, this.width, this.height);
			}
		}
	}

	@Override
	public void renderTooltip(GuiGraphics transform, int mouseX, int mouseY) {
		if(this.screen.getWrapper() instanceof IAmmoBackpack ammoBackpack && ammoBackpack.canStoreAmmo()) {
			if(this.inButton(mouseX, mouseY)) {
				transform.renderTooltip(this.screen.getFont(), Component.translatable("screen.travelersbackpack.tac_button"), mouseX, mouseY);
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(this.screen.getWrapper() instanceof IAmmoBackpack ammoBackpack &&
				ammoBackpack.canStoreAmmo() && this.inButton((int)mouseX, (int)mouseY)) {
			byte screenID = (byte)this.screen.getWrapper().getScreenID();
			if(this.screen.getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
				Objects.requireNonNull(Minecraft.getInstance().player).connection.send(new ServerboundOpenTacBackpackPacket(
						ServerboundOpenTacBackpackPacket.TYPE_BACKPACK_TO_TAC_SLOT, screenID, Optional.of(this.screen.getWrapper().backpackPos)
				));
			} else {
				Objects.requireNonNull(Minecraft.getInstance().player).connection.send(new ServerboundOpenTacBackpackPacket(
						ServerboundOpenTacBackpackPacket.TYPE_BACKPACK_TO_TAC_SLOT, screenID
				));
			}
			return true;
		}

		return false;
	}
}
