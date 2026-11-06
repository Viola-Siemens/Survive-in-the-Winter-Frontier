package com.hexagram2021.misc_twf_adventure.client.screen;

import com.hexagram2021.misc_twf_adventure.common.menu.BackpackAmmoHandler;
import com.hexagram2021.misc_twf_adventure.common.network.ServerboundOpenTacBackpackPacket;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.buttons.Button;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.Optional;

import static com.hexagram2021.misc_twf_adventure.MiscTwfAdventure.CONTENT_NAMESPACE;

/**
 * 旅行背包界面中的 TAC 弹药槽按钮喵~
 *
 * <p>当背包已升级为战术版本时，在背包界面显示此按钮；点击后向服务端发送切换数据包喵~</p>
 *
 * @author liudongyu
 */
public class TacButton extends Button {
	private static final ResourceLocation EXTRAS_TAC_TRAVELERS_BACKPACK = ResourceLocation.fromNamespaceAndPath(CONTENT_NAMESPACE, "textures/gui/container/travelers_backpack_tac.png");

	/**
	 * 构造按钮喵~
	 *
	 * @param screen 背包界面喵~
	 */
	public TacButton(BackpackScreen screen) {
		super(screen, 16, 73 + screen.visibleRows * 18, 18, 18);
	}

	@Override
	public void render(GuiGraphics transform, int mouseX, int mouseY, float partialTicks) {
		if(BackpackAmmoHandler.canStoreAmmo(this.screen.getWrapper())) {
			if(this.inButton(mouseX, mouseY)) {
				transform.blit(EXTRAS_TAC_TRAVELERS_BACKPACK, this.screen.getGuiLeft() + this.x, this.screen.getGuiTop() + this.y, 19, 0, this.width, this.height);
			} else {
				transform.blit(EXTRAS_TAC_TRAVELERS_BACKPACK, this.screen.getGuiLeft() + this.x, this.screen.getGuiTop() + this.y, 0, 0, this.width, this.height);
			}
		}
	}

	@Override
	public void renderTooltip(GuiGraphics transform, int mouseX, int mouseY) {
		if(BackpackAmmoHandler.canStoreAmmo(this.screen.getWrapper()) && this.inButton(mouseX, mouseY)) {
			transform.renderTooltip(this.screen.getFont(), Component.translatable("screen.travelersbackpack.tac_slot"), mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(BackpackAmmoHandler.canStoreAmmo(this.screen.getWrapper()) && this.inButton((int) mouseX, (int) mouseY)) {
			int screenId = this.screen.getWrapper().getScreenID();
			Objects.requireNonNull(Minecraft.getInstance().player).connection.send(
					screenId == Reference.BLOCK_ENTITY_SCREEN_ID
							? new ServerboundOpenTacBackpackPacket(ServerboundOpenTacBackpackPacket.TYPE_BACKPACK_TO_TAC_SLOT,
									screenId, Optional.of(this.screen.getWrapper().getBackpackPos()))
							: new ServerboundOpenTacBackpackPacket(ServerboundOpenTacBackpackPacket.TYPE_BACKPACK_TO_TAC_SLOT, screenId)
			);
			return true;
		}
		return false;
	}
}