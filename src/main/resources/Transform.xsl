<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output omit-xml-declaration="no" method="xml"></xsl:output>

    <xsl:template match="LifecycleEventHandler">
        <Callback>
            <xsl:attribute name="FullName">
                <xsl:value-of select="Location/@FileName"/>:<xsl:value-of select="Location/@LineNumber"/>:<xsl:value-of select="@Name"/>
            </xsl:attribute>
            <xsl:attribute name="Name">
                <xsl:value-of select="@Name"/>
            </xsl:attribute>
        </Callback>
    </xsl:template>

    <xsl:template match="LifecycleAwareComponent">
        <Component>
            <xsl:attribute name="FullName">
                <xsl:value-of select="Location/@FileName"/>:<xsl:value-of select="Location/@LineNumber"/>:<xsl:value-of select="@Name"/>
            </xsl:attribute>
            <xsl:attribute name="Name">
                <xsl:value-of select="@Name"/>
            </xsl:attribute>
            <xsl:apply-templates select="LifecycleEventHandler" />
        </Component>
    </xsl:template>

    <xsl:template match="Lifecycle">
        <Lifecycle>
            <xsl:apply-templates select="LifecycleAwareComponent" />
        </Lifecycle>
    </xsl:template>
</xsl:stylesheet>