package com.hexagram2021.misc_twf_zombie_animals.common.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import static com.hexagram2021.misc_twf_zombie_animals.MiscTwfZombieAnimals.MODID;

/**
 * 僵尸动物模块日志工具类，封装 Log4j Logger，提供统一的日志输出接口喵~
 *
 * @author liudongyu
 */
@SuppressWarnings("unused")
public final class MISCTWFLogger {
	private static final Logger LOGGER = LogManager.getLogger(MODID);

	/**
	 * 按指定日志级别输出日志喵~
	 *
	 * @param logLevel 日志级别喵~
	 * @param object   日志内容喵~
	 */
	public static void log(Level logLevel, @Nullable Object object) {
		LOGGER.log(logLevel, object);
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
	 * 输出 WARN 级别日志喵~
	 *
	 * @param object 日志内容喵~
	 */
	public static void warn(@Nullable Object object) {
		log(Level.WARN, object);
	}

	private MISCTWFLogger() {
	}
}
