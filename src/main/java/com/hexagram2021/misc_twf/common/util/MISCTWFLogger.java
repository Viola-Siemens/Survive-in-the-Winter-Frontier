package com.hexagram2021.misc_twf.common.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import static com.hexagram2021.misc_twf.SurviveInTheWinterFrontier.MODID;

/**
 * 模组日志工具类，封装 Log4j Logger，提供统一的日志输出接口喵~
 *
 * @author liudongyu
 */
@SuppressWarnings("unused")
public class MISCTWFLogger {
	private static final Logger LOGGER = LogManager.getLogger(MODID);

	/**
	 * 按指定日志级别输出日志喵~
	 *
	 * @param logLevel 日志级别喵~
	 * @param object   日志内容喵~
	 */
	public static void log(Level logLevel, @Nullable Object object) {
		LOGGER.log(logLevel, String.valueOf(object));
	}

	/**
	 * 输出 ERROR 级别日志喵~
	 *
	 * @param object 日志内容喵~
	 */
	public static void error(@Nullable Object object) {
		log(Level.ERROR, object);
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
	 * 输出 ERROR 级别格式化日志喵~
	 *
	 * @param message 日志消息模板喵~
	 * @param params  格式化参数喵~
	 */
	public static void error(String message, @Nullable Object... params) {
		LOGGER.log(Level.ERROR, message, params);
	}

	/**
	 * 输出 INFO 级别格式化日志喵~
	 *
	 * @param message 日志消息模板喵~
	 * @param params  格式化参数喵~
	 */
	public static void info(String message, @Nullable Object... params) {
		LOGGER.log(Level.INFO, message, params);
	}

	/**
	 * 输出 WARN 级别格式化日志喵~
	 *
	 * @param message 日志消息模板喵~
	 * @param params  格式化参数喵~
	 */
	public static void warn(String message, @Nullable Object... params) {
		LOGGER.log(Level.WARN, message, params);
	}

	/**
	 * 输出 DEBUG 级别日志喵~
	 *
	 * @param object 日志内容喵~
	 */
	public static void debug(@Nullable Object object) {
		log(Level.DEBUG, object);
	}
}
