package com.hexagram2021.misc_twf_hearing.command;

import com.hexagram2021.misc_twf_hearing.MiscTwfHearing;
import com.hexagram2021.misc_twf_hearing.api.HearingApi;
import com.hexagram2021.misc_twf_hearing.api.NoiseSnapshot;
import com.hexagram2021.misc_twf_hearing.api.NoiseSpec;
import com.hexagram2021.misc_twf_hearing.common.config.MISCTWFHearingConfig;
import com.hexagram2021.misc_twf_hearing.core.NoiseManager;
import com.hexagram2021.misc_twf_hearing.debug.HearingDebugState;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

/**
 * 循声调试命令喵~：`/hearing stats|debug|emit`，权限等级 2，输出不广播全服。
 *
 * <p>调试开关是**会话级运行时状态**（见 {@code HearingDebugState}），不写配置文件；
 * 日志级别仍由整合包 {@code log4j2.xml} 控制喵~</p>
 *
 * @author liudongyu
 */
@EventBusSubscriber(modid = MiscTwfHearing.MODID)
public final class HearingCommand {
	/**
	 * 注册 `/hearing` 命令喵~
	 *
	 * @param event 命令注册事件喵~
	 */
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		dispatcher.register(
				Commands.literal("hearing")
						.requires(source -> source.hasPermission(2))
						.then(Commands.literal("stats").executes(HearingCommand::executeStats))
						.then(Commands.literal("debug").then(
								Commands.argument("player", EntityArgument.player())
										.then(Commands.literal("on").executes(context -> executeDebug(context, true)))
										.then(Commands.literal("off").executes(context -> executeDebug(context, false)))
						)).then(Commands.literal("emit").then(
								Commands.argument("type", ResourceLocationArgument.id()).then(
										Commands.argument("radius", FloatArgumentType.floatArg(0.5F, 512.0F)).then(
												Commands.argument("strength", FloatArgumentType.floatArg(0.0F, 1.0F))
														.executes(HearingCommand::executeEmit).then(
																Commands.argument("lifetime", IntegerArgumentType.integer(1, 1048576))
																		.executes(HearingCommand::executeEmitWithLifetime)
														)
										)
								)
						))
		);
	}

	private static int executeStats(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		List<NoiseManager> managers = NoiseManager.all();
		if(managers.isEmpty()) {
			source.sendSuccess(() -> Component.translatable("commands.misc_twf_hearing.stats.empty"), false);
			return 0;
		}
		for(NoiseManager manager : managers) {
			NoiseManager.NoiseStats stats = manager.stats();
			ResourceLocation dimension = manager.level().dimension().location();
			source.sendSuccess(() -> Component.translatable("commands.misc_twf_hearing.stats",
					dimension.toString(), stats.activePulses(), stats.indexedChunks(), stats.emittedCount(),
					stats.mergedCount(), stats.queryCount(), stats.candidateCount()), false);
		}
		return managers.size();
	}

	private static int executeDebug(CommandContext<CommandSourceStack> context, boolean on) throws CommandSyntaxException {
		ServerPlayer target = EntityArgument.getPlayer(context, "player");
		if(on != HearingDebugState.isEnabled(target)) {
			HearingDebugState.toggle(target);
		}
		boolean enabled = HearingDebugState.isEnabled(target);
		context.getSource().sendSuccess(() -> Component.translatable("commands.misc_twf_hearing.debug",
				target.getDisplayName(), Component.translatable(enabled
						? "commands.misc_twf_hearing.debug.on" : "commands.misc_twf_hearing.debug.off")), true);
		return enabled ? 1 : 0;
	}

	private static int executeEmit(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		ResourceLocation type = ResourceLocationArgument.getId(context, "type");
		float radius = FloatArgumentType.getFloat(context, "radius");
		float strength = FloatArgumentType.getFloat(context, "strength");
		ServerLevel level = source.getLevel();
		Vec3 position = source.getPosition();
		int lifetime = MISCTWFHearingConfig.getSettings(type).rollLifetime(level.getRandom());
		return execute(source, type, level, position, radius, strength, lifetime);
	}

	private static int executeEmitWithLifetime(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		ResourceLocation type = ResourceLocationArgument.getId(context, "type");
		float radius = FloatArgumentType.getFloat(context, "radius");
		float strength = FloatArgumentType.getFloat(context, "strength");
		int lifetime = IntegerArgumentType.getInteger(context, "lifetime");
		ServerLevel level = source.getLevel();
		Vec3 position = source.getPosition();
		return execute(source, type, level, position, radius, strength, lifetime);
	}

	private static int execute(CommandSourceStack source, ResourceLocation type, ServerLevel level, Vec3 position,
							   float radius, float strength, int lifetime) {
		// 由实体执行时归属该实体（便于验证合并语义）；命令方块/控制台执行时 getEntity() 为空，按匿名噪声处理（§12.2 不合并）喵~
		Entity executor = source.getEntity();
		NoiseSnapshot snapshot = HearingApi.emit(level, position, new NoiseSpec(type, radius, strength, lifetime), executor);
		if(snapshot == null) {
			source.sendFailure(Component.translatable("commands.misc_twf_hearing.emit.rejected"));
			return 0;
		}
		source.sendSuccess(() -> Component.translatable(
				"commands.misc_twf_hearing.emit",
				snapshot.type().toString(), Long.toString(snapshot.id()), Float.toString(snapshot.radius()),
				Float.toString(snapshot.strength()), Long.toString(snapshot.expireTick())
		), true);
		return 1;
	}

	private HearingCommand() {
	}
}
