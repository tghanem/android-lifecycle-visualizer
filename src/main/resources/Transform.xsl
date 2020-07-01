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
            <xsl:attribute name="Icon">handler.png</xsl:attribute>
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
            <xsl:attribute name="Icon">component.png</xsl:attribute>
            <xsl:apply-templates select="LifecycleEventHandler" />
        </Component>
    </xsl:template>

    <xsl:template match="ApplicationLifecycle">
        <ApplicationLifecycle>
            <xsl:attribute name="Icon">root.png</xsl:attribute>
            <xsl:attribute name="ApplicationName">
                <xsl:value-of select="@ApplicationName" />
            </xsl:attribute>
            <xsl:apply-templates select="LifecycleAwareComponent" />
        </ApplicationLifecycle>
    </xsl:template>
</xsl:stylesheet>