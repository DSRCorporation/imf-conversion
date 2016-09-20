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
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:tt="http://www.w3.org/ns/ttml"
    xmlns:tts="http://www.w3.org/ns/ttml#styling"
    xmlns:ttp="http://www.w3.org/ns/ttml#parameter"
    version="2.0">

    <xsl:output method="xml" encoding="UTF-8" indent="yes" />

    <xsl:param name="oldStyleId" />
    <xsl:param name="newStyleId" />

    <xsl:variable name="matchesPattern" select="concat('(^|\W)', $oldStyleId, '(\W|$)')" />

    <xsl:template match="@style[matches(., $matchesPattern)]">
        <xsl:attribute name="style" select="replace(., $matchesPattern, concat('$1', $newStyleId, '$2'))" />
    </xsl:template>

    <!--
        copy attributes and elements as is
        should be latest templates
    -->
    <xsl:template match="@*">
        <xsl:copy-of select="." />
    </xsl:template>

    <xsl:template match="node()">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
