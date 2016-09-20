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
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tt="http://www.w3.org/ns/ttml"
    xmlns:tts="http://www.w3.org/ns/ttml#styling"
    xmlns:ttp="http://www.w3.org/ns/ttml#parameter"
    xmlns:nf="http://www.netflix.com/subtitles/functions"
    version="2.0"
    exclude-result-prefixes="nf">

    <xsl:output method="xml" encoding="UTF-8" indent="yes" />

    <xsl:param name="frameRatePar" />
    <xsl:param name="frameRateMultiplierPar" />

    <xsl:variable name="frameRateVar">
        <xsl:choose>
            <xsl:when test="$frameRatePar">
                <xsl:value-of select="$frameRatePar" />
            </xsl:when>
            <xsl:otherwise>30</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="frameRateMultiplierVar">
        <xsl:choose>
            <xsl:when test="$frameRateMultiplierPar">
                <xsl:value-of select="$frameRateMultiplierPar" />
            </xsl:when>
            <xsl:otherwise>1000 1001</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <!-- Entry point -->
    <xsl:template match="/">
        <xsl:if test="not(tt:tt)">
            <xsl:message terminate="yes">The root element tt is not found. It is an invalid TTML document.</xsl:message>
        </xsl:if>

        <xsl:variable name="lang" select="nf:get-language(/tt:tt/@xml:lang)" />
        <xsl:if test="not($supportedLanguages/lang[@code=$lang]/@code)">
            <xsl:message terminate="yes">The TTML document contains not supported language.</xsl:message>
        </xsl:if>

        <xsl:if test="not(tt:tt/tt:body)">
            <xsl:message terminate="yes">The body element of tt is not found. It is an invalid iTT document.</xsl:message>
        </xsl:if>

        <xsl:if test="not(tt:tt/tt:body/tt:div)">
            <xsl:message terminate="yes">No one div elemet specified. It is an invalid iTT document.</xsl:message>
        </xsl:if>

        <xsl:if test="tt:tt/tt:body/tt:div/tt:div">
            <xsl:message terminate="yes">Nested div elemets is not supported. It is an invalid iTT document.</xsl:message>
        </xsl:if>

        <xsl:if test="not(tt:tt/tt:body/*/tt:p)">
            <xsl:message terminate="yes">No one p elemet specified. It is an invalid iTT document.</xsl:message>
        </xsl:if>

        <xsl:apply-templates select="tt:tt" />
    </xsl:template>

    <xsl:template match="tt:tt">
        <xsl:copy>
            <!-- process tt attributes -->
            <xsl:apply-templates select="@*" />

            <!-- if required TT parameters are not set, add them -->
            <xsl:if test="not(@ttp:timeBase)">
                <xsl:attribute name="ttp:timeBase">smpte</xsl:attribute>
            </xsl:if>
            <xsl:if test="not(@ttp:frameRate)">
                <xsl:attribute name="ttp:frameRate" select="$frameRateVar" />
            </xsl:if>
            <xsl:if test="not(@ttp:frameRateMultiplier)">
                <xsl:attribute name="ttp:frameRateMultiplier" select="$frameRateMultiplierVar" />
            </xsl:if>
            <xsl:if test="not(@ttp:dropMode)">
                <xsl:attribute name="ttp:dropMode">nonDrop</xsl:attribute>
            </xsl:if>

            <!-- default head -->
            <xsl:if test="not(tt:head)">
                <xsl:sequence select="nf:get-default-head(.)" />
            </xsl:if>

            <!-- process head and body -->
            <xsl:apply-templates select="tt:head | tt:body" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@xml:lang[local-name(..) = 'tt']">
        <xsl:variable name="lang" select="nf:get-language(.)" />
        <xsl:choose>
            <xsl:when test=". = $lang">
                <xsl:copy />
            </xsl:when>
            <xsl:otherwise>
                <xsl:attribute name="xml:lang" select="$lang" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="@ttp:timeBase">
        <xsl:attribute name="ttp:timeBase">smpte</xsl:attribute>
    </xsl:template>

    <!--
        head processing
    -->
    <xsl:template match="tt:head">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            
            <xsl:apply-templates select="* except(tt:styling | tt:layout)" />

            <xsl:if test="not(tt:styling)">
                <xsl:sequence select="nf:get-default-head(.)/tt:styling" />
            </xsl:if>
            <xsl:apply-templates select="tt:styling"/>

            <xsl:if test="not(tt:layout)">
                <xsl:sequence select="nf:get-default-head(.)/tt:layout" />
            </xsl:if>
            <xsl:apply-templates select="tt:layout" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="tt:styling">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <xsl:apply-templates select="* except(tt:style)" />
            
            <xsl:if test="not(tt:style)">
                <xsl:sequence select="nf:get-default-head(.)/tt:styling/tt:style" />
            </xsl:if>
            <xsl:apply-templates select="tt:style" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="tt:style[local-name(..) = 'styling']">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            
            <xsl:if test="not(@tts:fontFamily)">
                <xsl:sequence select="nf:get-default-head(.)/tt:styling/tt:style/@tts:fontFamily" />
            </xsl:if>
            <xsl:if test="not(@tts:fontWeight)">
                <xsl:sequence select="nf:get-default-head(.)/tt:styling/tt:style/@tts:fontWeight" />
            </xsl:if>
            <xsl:if test="not(@tts:fontStyle)">
                <xsl:sequence select="nf:get-default-head(.)/tt:styling/tt:style/@tts:fontStyle" />
            </xsl:if>
            <xsl:if test="not(@tts:color)">
                <xsl:sequence select="nf:get-default-head(.)/tt:styling/tt:style/@tts:color" />
            </xsl:if>
            <xsl:if test="not(@tts:fontSize)">
                <xsl:sequence select="nf:get-default-head(.)/tt:styling/tt:style/@tts:fontSize" />
            </xsl:if>

            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="tt:layout">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <xsl:apply-templates select="* except(tt:region)" />

            <xsl:sequence select="nf:get-default-head(.)/tt:layout/tt:region" />
        </xsl:copy>
    </xsl:template>

    <!--
        body processing
    -->
    <xsl:template match="tt:body">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>

    <!--
        br processing
    -->
    <!-- remove more then one br from p -->
    <xsl:template match="tt:br[local-name(..) = 'p']">
        <xsl:if test="not(preceding-sibling::tt:br)">
            <xsl:copy-of select="." />
        </xsl:if>
    </xsl:template>

    <!-- replace all br to space from span -->
    <xsl:template match="tt:br[local-name(..) = 'span']">
        <xsl:text> </xsl:text>
    </xsl:template>

    <!--
        region attribute processing
    -->
    <!-- remove @region from span elements -->
    <xsl:template match="@region[local-name(..) = 'span']" />

    <!-- replace region to 'bottom' if value is not 'bottom' or 'top' -->
    <xsl:template match="@region[not(. = 'top') and not(. = 'bottom')]">
        <xsl:attribute name="region">bottom</xsl:attribute>
    </xsl:template>

    <!-- remove tts:extent and tts:origin from elements where @region is not 'top' or 'bottom' -->
    <xsl:template match="@tts:extent[not(../@region = 'top') and not(../@region = 'bottom')]
        | @tts:origin[not(../@region = 'top') and not(../@region = 'bottom')]" />

    <!--
        attributes of TT Style namespace processing
    -->
    <xsl:template match="@tts:fontFamily">
        <xsl:attribute name="tts:fontFamily" select="nf:get-default-head(..)/tt:styling/tt:style/@tts:fontFamily" />
    </xsl:template>

    <xsl:template match="@tts:fontWeight[not(. = 'normal') and not(. = 'bold')]">
        <xsl:attribute name="tts:fontWeight" select="nf:get-default-head(..)/tt:styling/tt:style/@tts:fontWeight" />
    </xsl:template>

    <xsl:template match="@tts:fontStyle[not(. = 'normal') and not(. = 'italic')]">
        <xsl:attribute name="tts:fontStyle" select="nf:get-default-head(..)/tt:styling/tt:style/@tts:fontStyle" />
    </xsl:template>

    <xsl:template match="@tts:color">
        <xsl:attribute name="tts:color">
            <xsl:choose> 
                <xsl:when test="not(matches(., '^rgb\([1-2]?\d?\d,[1-2]?\d?\d,[1-2]?\d?\d\)$', 'x'))
                    and not(matches(., '^#[0-9A-Fa-f]{6}$', 'x')) and not(matches(., '^[a-zA-Z]+$', 'x'))">
                    <xsl:value-of select="nf:get-default-head(..)/tt:styling/tt:style/@tts:color" />
                </xsl:when>

                <xsl:otherwise>
                    <xsl:value-of select="." />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="@tts:fontSize[not(. = '100%') and not(. = '1em')]">
        <xsl:attribute name="tts:fontSize" select="nf:get-default-head(..)/tt:styling/tt:style/@tts:fontSize" />
    </xsl:template>

    <xsl:template match="@tts:lineHeight">
        <xsl:attribute name="tts:lineHeight">100%</xsl:attribute>
    </xsl:template>

    <!--
        remove unused elements/attributes
    -->
    <xsl:template match="@timeContainer
        | @ttp:markerMode 
        | @tts:backgroundColor
        | @tts:direction
        | @tts:unicodeBidi        
        | tt:set
        | @tts:display
        | @tts:showBackground
        | @tts:dynamicFlow
        | @tts:zIndex
        | @tts:writingMode
        | @tts:visibility
        | @tts:opacity
        | @tts:padding
        | @tts:textOutline
        | @ttp:cellResolution
        | @tts:textDecoration[not(. = 'none')
                              and not(. = 'underline')
                              and not(. = 'noUnderline')
                              and not(. = 'noOverline')
                              and not(. = 'noLineThrough')]
        | @tts:textAlign
        | @tts:displayAlign[not(. = 'before') and not(. = 'after')]" />

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

    <xsl:variable name="supportedLanguages">
        <lang code="da"     desc="Danish" />
        <lang code="nl"     desc="Dutch" />
        <lang code="en-AU"  desc="English Australia" />
        <lang code="en-CA"  desc="English Canada" />
        <lang code="en-GB"  desc="English United Kingdom" />
        <lang code="en-US"  desc="English United States" />
        <lang code="fi"     desc="Finnish" />
        <lang code="fr-CA"  desc="French Canada" />
        <lang code="fr-FR"  desc="French France" />
        <lang code="de"     desc="German" />
        <lang code="de-DE"  desc="German Germany" />
        <lang code="el"     desc="Greek" />
        <lang code="it"     desc="Italian" />
        <lang code="no"     desc="Norwegian" />
        <lang code="pl"     desc="Polish" />
        <lang code="pt-BR"  desc="Portuguese Brazil" />
        <lang code="pt-PT"  desc="Portuguese Portugal" />
        <lang code="es-419" desc="Spanish Latin America" />
        <lang code="es-MX"  desc="Spanish Mexico" />
        <lang code="es-ES"  desc="Spanish Spain" />
        <lang code="sv"     desc="Swedish" />
    </xsl:variable>

    <xsl:variable name="langWithoutCountry">
        <lang code="en" value="en-US" />
        <lang code="fr" value="fr-FR" />
        <lang code="pt" value="pt-PT" />
        <lang code="es" value="es-ES" />
    </xsl:variable>

    <xsl:function name="nf:get-language">
        <xsl:param name="lang" />
        <xsl:value-of select="if ($langWithoutCountry/lang[@code=$lang]/@value) then
            $langWithoutCountry/lang[@code=$lang]/@value else $lang" />
    </xsl:function>

    <xsl:function name="nf:get-default-head">
        <xsl:param name="node" />
        <xsl:element name="head" namespace="{namespace-uri($node)}">
            <xsl:element name="styling" namespace="{namespace-uri($node)}">
                <xsl:element name="style" namespace="{namespace-uri($node)}">
                    <xsl:attribute name="xml:id">normal</xsl:attribute>
                    <xsl:attribute name="tts:fontFamily">sansSerif</xsl:attribute>
                    <xsl:attribute name="tts:fontWeight">normal</xsl:attribute>
                    <xsl:attribute name="tts:fontStyle">normal</xsl:attribute>
                    <xsl:attribute name="tts:color">white</xsl:attribute>
                    <xsl:attribute name="tts:fontSize">100%</xsl:attribute>
                </xsl:element>
            </xsl:element>
            <xsl:element name="layout" namespace="{namespace-uri($node)}">
                <xsl:element name="region" namespace="{namespace-uri($node)}">
                    <xsl:attribute name="xml:id">top</xsl:attribute>
                    <xsl:attribute name="tts:origin">0% 0%</xsl:attribute>
                    <xsl:attribute name="tts:extent">100% 15%</xsl:attribute>
                    <xsl:attribute name="tts:textAlign">center</xsl:attribute>
                    <xsl:attribute name="tts:displayAlign">before</xsl:attribute>
                </xsl:element>
                <xsl:element name="region" namespace="{namespace-uri($node)}">
                    <xsl:attribute name="xml:id">bottom</xsl:attribute>
                    <xsl:attribute name="tts:origin">0% 85%</xsl:attribute>
                    <xsl:attribute name="tts:extent">100% 15%</xsl:attribute>
                    <xsl:attribute name="tts:textAlign">center</xsl:attribute>
                    <xsl:attribute name="tts:displayAlign">after</xsl:attribute>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:function>

    <!-- ignore text elements -->
    <!--<xsl:template match="text()" />-->
</xsl:stylesheet>
