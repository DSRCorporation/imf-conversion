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

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Custom logger. It's a wrapper on slf4j logger which improves messages
 * (in particular, fixes end of line separator).
 */
public class ImfLogger implements Logger {

    private final Logger logger;

    public ImfLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        logger.trace(fixMsg(msg));
    }

    @Override
    public void trace(String format, Object arg) {
        logger.trace(fixMsg(format), arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        logger.trace(fixMsg(format), arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        logger.trace(fixMsg(format), arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        logger.trace(fixMsg(msg), t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        logger.trace(marker, msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        logger.trace(marker, format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        logger.trace(marker, format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        logger.trace(marker, format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        logger.trace(marker, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        logger.debug(fixMsg(msg));
    }

    @Override
    public void debug(String format, Object arg) {
        logger.debug(fixMsg(format), arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        logger.debug(fixMsg(format), arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        logger.debug(fixMsg(format), arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        logger.debug(fixMsg(msg), t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        logger.debug(marker, fixMsg(msg));
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        logger.debug(marker, fixMsg(format), arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        logger.debug(marker, fixMsg(format), arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        logger.debug(marker, fixMsg(format), arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        logger.debug(marker, fixMsg(msg), t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        logger.info(fixMsg(msg));
    }

    @Override
    public void info(String format, Object arg) {
        logger.info(fixMsg(format), arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        logger.info(fixMsg(format), arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        logger.info(fixMsg(format), arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        logger.info(fixMsg(msg), t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    @Override
    public void info(Marker marker, String msg) {
        logger.info(marker, fixMsg(msg));
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        logger.info(marker, fixMsg(format), arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        logger.info(marker, fixMsg(format), arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        logger.info(marker, fixMsg(format), arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        logger.info(marker, fixMsg(msg), t);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        logger.warn(fixMsg(msg));
    }

    @Override
    public void warn(String format, Object arg) {
        logger.warn(fixMsg(format), arg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        logger.warn(fixMsg(format), arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        logger.warn(fixMsg(format), arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
        logger.warn(fixMsg(msg), t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        logger.warn(marker, fixMsg(msg));
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        logger.warn(marker, fixMsg(format), arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        logger.warn(marker, fixMsg(format), arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        logger.warn(marker, fixMsg(format), arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {

    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        logger.error(fixMsg(msg));
    }

    @Override
    public void error(String format, Object arg) {
        logger.error(fixMsg(format), arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        logger.error(fixMsg(format), arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        logger.error(fixMsg(format), arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.error(fixMsg(msg), t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        logger.error(marker, fixMsg(msg));
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        logger.error(marker, fixMsg(format), arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        logger.error(marker, fixMsg(format), arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        logger.error(marker, fixMsg(format), arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        logger.error(marker, fixMsg(msg), t);
    }

    private String fixMsg(String msg) {
        return LogHelper.fixEndLine(msg);
    }

}
