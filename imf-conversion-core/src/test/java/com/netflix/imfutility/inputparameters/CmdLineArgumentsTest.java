/**
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
package com.netflix.imfutility.inputparameters;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests that command line arguments common for all formats are parsed correctly.
 */
public class CmdLineArgumentsTest {

    @Test
    public void testParseCorrectCmdLineArgumentsShortName() {
        String[] args = new String[]{
                "--cpl", "cpl.xml", "--imp", "pathToImp", "-c", "config.xml", "-w", "pathToWorkingDir", "-l", "error"
        };
        ImfUtilityCmdLineArgs cmdLineArgs = CliFactory.parseArguments(ImfUtilityCmdLineArgs.class, args);

        assertEquals("cpl.xml", cmdLineArgs.getCpl());
        assertEquals("pathToImp", cmdLineArgs.getImp());
        assertEquals("config.xml", cmdLineArgs.getConfig());
        assertEquals("pathToWorkingDir", cmdLineArgs.getWorkingDirectory());
        assertEquals(LogLevel.error, cmdLineArgs.getLogLevel());
    }

    @Test
    public void testParseCorrectCmdLineArgumentsLongName() {
        String[] args = new String[]{
                "--cpl", "cpl.xml", "--imp", "pathToImp", "--config", "config.xml", "--working-dir", "pathToWorkingDir",
                "--log-level", "debug"
        };
        ImfUtilityCmdLineArgs cmdLineArgs = CliFactory.parseArguments(ImfUtilityCmdLineArgs.class, args);

        assertEquals("cpl.xml", cmdLineArgs.getCpl());
        assertEquals("pathToImp", cmdLineArgs.getImp());
        assertEquals("config.xml", cmdLineArgs.getConfig());
        assertEquals("pathToWorkingDir", cmdLineArgs.getWorkingDirectory());
        assertEquals(LogLevel.debug, cmdLineArgs.getLogLevel());
    }

    @Test(expected = HelpRequestedException.class)
    public void testParseHelpShort() {
        String[] args = new String[]{"-h"};
        CliFactory.parseArguments(ImfUtilityCmdLineArgs.class, args);
    }

    @Test(expected = HelpRequestedException.class)
    public void testParseHelpLong() {
        String[] args = new String[]{"--help"};
        CliFactory.parseArguments(ImfUtilityCmdLineArgs.class, args);
    }

    @Test(expected = ArgumentValidationException.class)
    public void testExceptionOnUnknownCmdLineArgument() {
        String[] args = new String[]{"--xxx", "xxx"};
        CliFactory.parseArguments(ImfUtilityCmdLineArgs.class, args);
    }

    @Test(expected = ArgumentValidationException.class)
    public void testExceptionOnInvalidFormat() {
        String[] args = new String[]{"-f", "xxx"};
        CliFactory.parseArguments(ImfUtilityCmdLineArgs.class, args);
    }

}
