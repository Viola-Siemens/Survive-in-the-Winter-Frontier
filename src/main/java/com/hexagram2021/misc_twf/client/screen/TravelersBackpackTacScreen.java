package com.hexagram2021.misc_twf.client.screen;

import com.hexagram2021.misc_twf.common.menu.AbstractTravelersBackpackTacMenu;
import com.hexagram2021.misc_twf.common.network.ServerboundOpenTacBackpackPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Objects;
import java.util.Optional;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 旅行背包 TAC 弹药槽的客户端 GUI 界面喵~
 * 显示 9 格弹药存储槽位，并提供返回背包主界面的按钮喵~
 * 仅当旅行背包已升级为 TAC 版本时可用喵~
 *
 * @author liudongyu
 */
@OnlyIn(Dist.CLIENT)
public class TravelersBackpackTacScreen extends AbstractContainerScreen<AbstractTravelersBackpackTacMenu> implements MenuAccess<AbstractTravelersBackpackTacMenu> {
	private static final ResourceLocation EXTRAS_TAC_TRAVELERS_BACKPACK = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/container/travelers_backpack_tac.png");
	private static final ResourceLocation TRAVELERS_BACKPACK_TAC_SLOT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/container/travelers_backpack_tac_slot.png");

	private static final int BACK_BUTTON_X = 152;
	private static final int BACK_BUTTON_Y = 42;
	private static final int BACK_BUTTON_WIDTH = 18;
	private static final int BACK_BUTTON_HEIGHT = 18;

	private final byte screenId;

	public TravelersBackpackTacScreen(AbstractTravelersBackpackTacMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		this.screenId = (byte) menu.getWrapper().getScreenID();
		this.imageWidth = 176;
		this.imageHeight = 144;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	public void render(GuiGraphics transform, int mouseX, int mouseY, float partialTicks) {
		super.render(transform, mouseX, mouseY, partialTicks);
		this.renderTooltip(transform, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics transform, float partialTicks, int mouseX, int mouseY) {
		transform.blit(TRAVELERS_BACKPACK_TAC_SLOT, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

		if(this.leftPos + BACK_BUTTON_X <= mouseX && mouseX < this.leftPos + BACK_BUTTON_X + BACK_BUTTON_WIDTH &&
				this.topPos + BACK_BUTTON_Y <= mouseY && mouseY < this.topPos + BACK_BUTTON_Y + BACK_BUTTON_HEIGHT) {
			transform.blit(EXTRAS_TAC_TRAVELERS_BACKPACK, this.leftPos + BACK_BUTTON_X, this.topPos + BACK_BUTTON_Y, 19, 19, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT);
		} else {
			transform.blit(EXTRAS_TAC_TRAVELERS_BACKPACK, this.leftPos + BACK_BUTTON_X, this.topPos + BACK_BUTTON_Y, 0, 19, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT);
		}
	}

	@Override
	protected void renderTooltip(GuiGraphics transform, int mouseX, int mouseY) {
		super.renderTooltip(transform, mouseX, mouseY);

		if(this.leftPos + BACK_BUTTON_X <= mouseX && mouseX < this.leftPos + BACK_BUTTON_X + BACK_BUTTON_WIDTH &&
				this.topPos + BACK_BUTTON_Y <= mouseY && mouseY < this.topPos + BACK_BUTTON_Y + BACK_BUTTON_HEIGHT) {
			transform.renderTooltip(this.font, Component.translatable("screen.travelersbackpack.tac_back"), mouseX, mouseY);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(this.leftPos + BACK_BUTTON_X <= mouseX && mouseX < this.leftPos + BACK_BUTTON_X + BACK_BUTTON_WIDTH &&
				this.topPos + BACK_BUTTON_Y <= mouseY && mouseY < this.topPos + BACK_BUTTON_Y + BACK_BUTTON_HEIGHT) {
			LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
			if(this.screenId == Reference.BLOCK_ENTITY_SCREEN_ID) {
				player.connection.send(new ServerboundOpenTacBackpackPacket(
						ServerboundOpenTacBackpackPacket.TYPE_TAC_SLOT_TO_BACKPACK, this.screenId, Optional.of(this.menu.getWrapper().getBackpackPos())
				));
			} else {
				player.connection.send(new ServerboundOpenTacBackpackPacket(
						ServerboundOpenTacBackpackPacket.TYPE_TAC_SLOT_TO_BACKPACK, this.screenId
				));
			}
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
}
