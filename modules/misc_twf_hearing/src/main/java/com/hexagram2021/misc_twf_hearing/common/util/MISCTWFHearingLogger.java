package com.hexagram2021.misc_twf_hearing.common.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

import static com.hexagram2021.misc_twf_hearing.MiscTwfHearing.MODID;

/**
 * 循声模块日志工具类，封装 Log4j Logger 喵~
 *
 * <p>是否需要输出 DEBUG 日志由整合包的 {@code log4j2.xml} 级别控制，本模块**不提供日志开关配置**；
 * 需要拼接昂贵字符串时请使用 {@link #debug(Supplier)}，它会先检查级别再求值喵~</p>
 *
 * <p>本类**不持有任何限频状态**：限频游标由业务对象各自持有（见 {@link TickRateLimiter}），
 * 避免不同类别的告警互相抢额度喵~</p>
 *
 * @author liudongyu
 */
@SuppressWarnings("unused")
public final class MISCTWFHearingLogger {
	private static final Logger LOGGER = LogManager.getLogger(MODID);

	/**
	 * 按指定级别输出日志喵~
	 *
	 * @param logLevel 日志级别喵~
	 * @param object   日志内容喵~
	 */
	public static void log(Level logLevel, @Nullable Object object) {
		LOGGER.log(logLevel, object);
	}

	/**
	 * 判断是否启用了 DEBUG 级别的日志输出喵~
	 *
	 * @return 是否启用 DEBUG 日志喵~
	 */
	public static boolean isDebugEnabled() {
		return LOGGER.isDebugEnabled();
	}

	/**
	 * 输出 DEBUG 级别日志（内容为常量或已拼接好的字符串时使用）喵~
	 *
	 * @param object 日志内容喵~
	 */
	public static void debug(@Nullable Object object) {
		if(LOGGER.isDebugEnabled()) {
			log(Level.DEBUG, object);
		}
	}

	/**
	 * 输出 DEBUG 级别日志（仅在启用 DEBUG 时才求值，避免无谓的字符串拼接开销）喵~
	 *
	 * @param message 日志内容提供者喵~
	 */
	public static void debug(Supplier<?> message) {
		if(LOGGER.isDebugEnabled()) {
			log(Level.DEBUG, message.get());
		}
	}

	/**
	 * 输出 INFO 级别日志喵~
	 *
	 * @param object 日志内容喵~
	 */
	public static void info(@Nullable Object object) {
		log(Level.INFO, object);
	}

	/**
	 * 输出 WARN 级别日志喵~
	 *
	 * @param object 日志内容喵~
	 */
	public static void warn(@Nullable Object object) {
		log(Level.WARN, object);
	}

	/**
	 * 输出 ERROR 级别日志喵~
	 *
	 * @param object 日志内容喵~
	 */
	public static void error(@Nullable Object object) {
		log(Level.ERROR, object);
	}

	private MISCTWFHearingLogger() {
	}
}
