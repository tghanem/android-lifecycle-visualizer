<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/1999/XSL/Transform">
    <xsl:output omit-xml-declaration="no" method="xml"></xsl:output>

    <xsl:template name="ProcessLifecycleEventHandler">
        <xsl:param name="LifecycleEventHandler" />
        <xsl:param name=""
    </xsl:template>

    <xsl:template match="LifecycleEventHandler[@Name=onCreate]">
        <Node>
            <xsl:copy-of select="current()" />
            <MoveNext>
                <xsl:apply-templates select="LifecycleEventHandler[@Name=onStart]" />
            </MoveNext>
        </Node>
    </xsl:template>

    <xsl:template match="LifecycleEventHandler[@Name=onStart]">
        <Node>
            <xsl:copy-of select="current()" />
            <MoveNext>

            </MoveNext>
        </Node>
    </xsl:template>
</xsl:stylesheet>