<!--

    Copyright (C) 2016 Netflix, Inc.

        This file is part of IMF Conversion Utility.

        IMF Conversion Utility is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        IMF Conversion Utility is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.

-->
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:t="http://metadata.dpp.imfutility.netflix.com">
    <xsl:output method="text" encoding="UTF-8" omit-xml-declaration="yes"/>

    <xsl:param name="framework" />

    <!--
        A list with dpp framework nodes.
    -->
    <xsl:variable name="dppNodes">
        <item>ProductionNumber</item>
        <item>Synopsis</item>
        <item>Originator</item>
        <item>CopyrightYear</item>
        <item>OtherIdentifier</item>
        <item>OtherIdentifierType</item>
        <item>Genre</item>
        <item>Distributor</item>
        <item>PictureRatio</item>
        <item>ThreeD</item>
        <item>3D</item>
        <item>ThreeDType</item>
        <item>3DType</item>
        <item>ProductPlacement</item>
        <item>PSEPass</item>
        <item>PSEManufacturer</item>
        <item>PSEVersion</item>
        <item>VideoComments</item>
        <item>SecondaryAudioLanguage</item>
        <item>TertiaryAudioLanguage</item>
        <item>AudioLoudnessStandard</item>
        <item>AudioComments</item>
        <item>LineUpStart</item>
        <item>IdentClockStart</item>
        <item>TotalNumberOfParts</item>
        <item>TotalProgrammeDuration</item>
        <item>AudioDescriptionPresent</item>
        <item>AudioDescriptionType</item>
        <item>OpenCaptionsPresent</item>
        <item>OpenCaptionsType</item>
        <item>OpenCaptionsLanguage</item>
        <item>SigningPresent</item>
        <item>SignLanguage</item>
        <item>CompletionDate</item>
        <item>TextlessElementsExist</item>
        <item>ProgrammeHasText</item>
        <item>ProgrammeTextLanguage</item>
        <item>ContactEmail</item>
        <item>ContactTelephoneNumber</item>
    </xsl:variable>

    <!--
        A list with as11 core framework nodes.
    -->
    <xsl:variable name="as11Nodes">
        <item>SeriesTitle</item>
        <item>ProgrammeTitle</item>
        <item>EpisodeTitleNumber</item>
        <item>ShimName</item>
        <item>ShimVersion</item>
        <item>AudioTrackLayout</item>
        <item>PrimaryAudioLanguage</item>
        <item>ClosedCaptionsPresent</item>
        <item>ClosedCaptionsType</item>
        <item>ClosedCaptionsLanguage</item>
    </xsl:variable>

    <!--
        A template to convert human-readable enum values to numbers for BMXLib
    -->
    <xsl:template name="putValue">
        <xsl:variable name="nodename" select="local-name()" />
        <xsl:variable name="stringName" select="current()" />
        <xsl:variable name="enumValue" select="$toIntEnumerations/enum[@name=$nodename]/item[.=$stringName]/@value" />

        <xsl:choose>
            <xsl:when test="$enumValue">
                <xsl:value-of select="$enumValue"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$stringName" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!--
        A map of Human-readable enum values to numbers for BMXLib
    -->
    <xsl:variable name="toIntEnumerations">
        <enum name="PictureRatio">
            <item value="4/3">4:3</item>
            <item value="14/9">14:9</item>
            <item value="15/9">15:9</item>
            <item value="16/9">16:9</item>
            <item value="37/20">37:20</item>
            <item value="21/9">21:9</item>
            <item value="12/5">12:5</item>
        </enum>
        <enum name="PSEPass">
            <item value="0">Yes</item>
            <item value="1">No</item>
            <item value="2">Not tested</item>
        </enum>
        <enum name="ThreeDType">
            <item value="0">Side by side</item>
            <item value="1">Dual</item>
            <item value="2">Left eye only</item>
            <item value="3">Right eye only</item>
        </enum>
        <enum name="AudioTrackLayout">
            <item value="3">EBU R 48: 2a</item>
            <item value="9">EBU R 48: 4b</item>
            <item value="10">EBU R 48: 4c</item>
            <item value="49">EBU R 123: 16c</item>
            <item value="50">EBU R 123: 16d</item>
            <item value="52">EBU R 123: 16f</item>
        </enum>
        <enum name="AudioLoudnessStandard">
            <item value="0">None</item>
            <item value="1">EBU R 128</item>
        </enum>
        <enum name="AudioDescriptionType">
            <item value="0">Control data / Narration</item>
            <item value="1">AD Mix</item>
        </enum>
        <enum name="ClosedCaptionsType">
            <item value="0">Hard of Hearing</item>
            <item value="1">Translation</item>
        </enum>
        <enum name="OpenCaptionsType">
            <item value="0">Hard of Hearing</item>
            <item value="1">Translation</item>
        </enum>
        <enum name="SigningPresent">
            <item value="0">Yes</item>
            <item value="1">No</item>
            <item value="2">Signer only</item>
        </enum>
        <enum name="SignLanguage">
            <item value="0">BSL (British Sign Language)</item>
            <item value="1">BSL (Makaton)</item>
        </enum>
    </xsl:variable>

    <!--
        A template to select particular framework's nodes.
    -->
    <xsl:template name="iterateNodes">
        <xsl:if test="$framework='UKDPP'">
            <xsl:if test="index-of($dppNodes/item, local-name())">
                <xsl:value-of select="local-name()"/><xsl:text>: </xsl:text><xsl:call-template name="putValue"/><xsl:text>&#xa;</xsl:text>
            </xsl:if>
        </xsl:if>

        <xsl:if test="$framework='AS11Core'">
            <xsl:if test="index-of($as11Nodes/item, local-name())">
                <xsl:value-of select="local-name()"/><xsl:text>: </xsl:text><xsl:call-template name="putValue"/><xsl:text>&#xa;</xsl:text>
            </xsl:if>
        </xsl:if>

        <xsl:if test="$framework='AS11Segmentation'">
            <xsl:if test="local-name()='Parts'">
                <xsl:for-each select="current()/*:Part">
                    <xsl:value-of select="*:PartNumber"/><xsl:text>/</xsl:text><xsl:value-of select="*:PartTotal"/><xsl:text> </xsl:text><xsl:value-of select="*:PartSOM"/><xsl:text> </xsl:text><xsl:value-of select="*:PartDuration"/><xsl:text>&#xa;</xsl:text>
                </xsl:for-each>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <!--
        Entry point.
    -->
    <xsl:template match="/">
        <xsl:for-each select="*:Dpp/*:Editorial/*">
            <xsl:call-template name="iterateNodes"/>
        </xsl:for-each>
        <xsl:for-each select="*:Dpp/*:Technical/*/*">
            <xsl:call-template name="iterateNodes"/>
        </xsl:for-each>
        <xsl:for-each select="*:Dpp/*:Technical/*">
            <xsl:call-template name="iterateNodes"/>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>