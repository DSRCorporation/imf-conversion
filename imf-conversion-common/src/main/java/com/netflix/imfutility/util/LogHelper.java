/*
 * Copyright (C) 2016 Netflix, Inc.
 *
 *     This file is part of IMF Conversion Utility.
 *
 *     IMF Conversion Utility is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     IMF Conversion Utility is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.netflix.imfutility.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * Helper methods related to logging.
 */
public final class LogHelper {

    public static final String TAB = "  ";

    /**
     * Removes all end of lines unless it's debug log level.
     *
     * @param logMessage log message to be fixed
     * @return log message with replaced end of line
     */
    public static String fixEndLine(String logMessage) {
        if (getLogLevel() == Level.DEBUG) {
            return logMessage;
        }
        return logMessage.replaceAll("\n", "");
    }

    /**
     * Sets root log level.
     *
     * @param logLevel root log level
     */
    public static void setLogLevel(Level logLevel) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(logLevel);
        ctx.updateLoggers();
    }

    /**
     * Gets root log level.
     *
     * @return root log level.
     */
    public static Level getLogLevel() {
        return ((LoggerContext) LogManager.getContext(false))
                .getConfiguration()
                .getLoggerConfig(LogManager.ROOT_LOGGER_NAME)
                .getLevel();
    }

    private LogHelper() {
    }
}
