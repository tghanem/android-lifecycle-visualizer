<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<xsl:for-each select="Activity">
			<Component>
				<xsl:attribute name="FullName">
					<xsl:value-of select="Location/@FileName"/>:<xsl:value-of select="Location/@LineNumber"/>:<xsl:value-of select="@Name"/>
				</xsl:attribute>
				<xsl:attribute name="Name">
					<xsl:value-of select="@Name"/>
				</xsl:attribute>				
			</Component>
		</xsl:for-each>	
	</xsl:template>
</xsl:stylesheet>