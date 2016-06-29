package com.netflix.imfutility.inputparameters;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import com.netflix.imfutility.Format;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests that command line arguments common for all formats are parsed correctly.
 */
public class CmdLineArgumentsTest {

    @Test
    public void testParseCorrectCmdLineArgumentsShortName() {
        String[] args = new String[]{
                "-f", "dpp", "--cpl", "cpl.xml", "--imp", "pathToImp", "-c", "config.xml", "-w", "pathToWorkingDir"
        };
        ImfUtilityCmdLineArgs cmdLineArgs = CliFactory.parseArguments(ImfUtilityCmdLineArgs.class, args);

        assertEquals(Format.dpp, cmdLineArgs.getFormat());
        assertEquals("cpl.xml", cmdLineArgs.getCpl());
        assertEquals("pathToImp", cmdLineArgs.getImp());
        assertEquals("config.xml", cmdLineArgs.getConfig());
        assertEquals("pathToWorkingDir", cmdLineArgs.getWorkingDirectory());
    }

    @Test
    public void testParseCorrectCmdLineArgumentsLongName() {
        String[] args = new String[]{
                "--format", "dpp", "--cpl", "cpl.xml", "--imp", "pathToImp", "--config", "config.xml", "--working-dir", "pathToWorkingDir"
        };
        ImfUtilityCmdLineArgs cmdLineArgs = CliFactory.parseArguments(ImfUtilityCmdLineArgs.class, args);

        assertEquals(Format.dpp, cmdLineArgs.getFormat());
        assertEquals("cpl.xml", cmdLineArgs.getCpl());
        assertEquals("pathToImp", cmdLineArgs.getImp());
        assertEquals("config.xml", cmdLineArgs.getConfig());
        assertEquals("pathToWorkingDir", cmdLineArgs.getWorkingDirectory());
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

    @Test(expected = ArgumentValidationException.class)
    public void testExceptionOnMissingRequiredCmdLineArgument() {
        String[] args = new String[]{"--cpl", "cpl.xml", "--imp", "pathToImp", "--config", "config.xml", "--working-dir", "pathToWorkingDir"};
        CliFactory.parseArguments(ImfUtilityCmdLineArgs.class, args);
    }


}
