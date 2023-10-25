package com.hexagram2021.misc_twf.common.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class MISCTWFLogger {
	public static boolean debugMode = true;
	public static Logger logger;

	public static void log(Level logLevel, @Nullable Object object) {
		logger.log(logLevel, String.valueOf(object));
	}

	public static void error(@Nullable Object object) {
		log(Level.ERROR, object);
	}

	public static void info(@Nullable Object object) {
		log(Level.INFO, object);
	}

	public static void warn(@Nullable Object object) {
		log(Level.WARN, object);
	}

	public static void error(String message, @Nullable Object... params) {
		logger.log(Level.ERROR, message, params);
	}

	public static void info(String message, @Nullable Object... params) {
		logger.log(Level.INFO, message, params);
	}

	public static void warn(String message, @Nullable Object... params) {
		logger.log(Level.WARN, message, params);
	}

	public static void debug(@Nullable Object object) {
		if(debugMode) {
			log(Level.INFO, "[DEBUG:] " + object);
		}
	}

	public static void debug(String format, @Nullable Object... params) {
		if(debugMode) {
			info("[DEBUG:] " + format, params);
		}
	}
}
