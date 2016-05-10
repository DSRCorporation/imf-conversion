<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text" encoding="UTF-8" omit-xml-declaration="yes"/>

    <xsl:param name="framework" />

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

    <xsl:template name="iterateNodes">
        <xsl:if test="$framework='UKDPP'">
            <xsl:if test="index-of($dppNodes/item, local-name())">
                <xsl:value-of select="local-name()"/><xsl:text>: </xsl:text><xsl:value-of select="current()"/><xsl:text>&#xa;</xsl:text>
            </xsl:if>
        </xsl:if>

        <xsl:if test="$framework='AS11Core'">
            <xsl:if test="index-of($as11Nodes/item, local-name())">
                <xsl:value-of select="local-name()"/><xsl:text>: </xsl:text><xsl:value-of select="current()"/><xsl:text>&#xa;</xsl:text>
            </xsl:if>
        </xsl:if>

        <xsl:if test="$framework='AS11Segmentation'">
            <xsl:if test="local-name()='Parts'">
                <xsl:for-each select="current()/Part">
                    <xsl:value-of select="PartNumber"/><xsl:text>/</xsl:text><xsl:value-of select="PartTotal"/><xsl:text> </xsl:text><xsl:value-of select="PartSOM"/><xsl:text> </xsl:text><xsl:value-of select="PartDuration"/><xsl:text>&#xa;</xsl:text>
                </xsl:for-each>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <xsl:template match="/">
        <xsl:for-each select="Dpp/Editorial/*">
            <xsl:call-template name="iterateNodes"/>
        </xsl:for-each>
        <xsl:for-each select="Dpp/Technical/*/*">
            <xsl:call-template name="iterateNodes"/>
        </xsl:for-each>
        <xsl:for-each select="Dpp/Technical/*">
            <xsl:call-template name="iterateNodes"/>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>