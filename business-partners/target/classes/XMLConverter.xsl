<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        version="2.0">

    <xsl:template match="/">
        <invoice xmlns="http://activemq.apache.org/camel/tutorial/partners/invoice">
            <partner-id>2</partner-id>
            <date-received>???</date-received>
            <xsl:for-each select="order/item">
                <line-item>
                    <product-id><xsl:value-of select="code"/></product-id>
                    <description><xsl:value-of select="name"/></description>
                    <quantity><xsl:value-of select="quantity"/></quantity>
                    <item-price><xsl:value-of select="price"/></item-price>
                    <order-date><xsl:value-of select="date"/></order-date>
                </line-item>
            </xsl:for-each>
            <order-total>???</order-total>
        </invoice>
    </xsl:template>
</xsl:transform>
