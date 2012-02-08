<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" indent="yes" encoding="iso-8859-1"/>
  
  <xsl:template match="/">
    <html>
      <head>
		<meta http-equiv="Cache-Control" content="no-cache" /> 
		<meta http-equiv="Pragma" content="no-cache" /> 
		<meta http-equiv="Expires" content="0" /> 
        <title>CSP IPC Configuration Help</title>
        <xsl:choose>
          <xsl:when test="Help/@css">
            <link rel="stylesheet" type="text/css" href="{Help/@css}"/>
          </xsl:when>
          <xsl:otherwise>
            <link rel="stylesheet" type="text/css" href="csp-ipc-help.css"/>
          </xsl:otherwise>
        </xsl:choose>
      </head>
      <body>
        <div id="frame">
          <h1>CSP IPC Configuration Help</h1>
            <xsl:for-each select="Help/Product">
              <h2>Product: <xsl:value-of select="@name"/> - Material ID: <xsl:value-of select="@matid"/></h2>
            </xsl:for-each>
            <xsl:apply-templates select="Help/Tab"/>
        </div>
        <div id="footer">
            <xsl:for-each select="Help/Contact">
              <p>Contact: <xsl:value-of select="@name"/> (<xsl:value-of select="@responsibility"/>)</p>
            </xsl:for-each>
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="Tab">
  	<div class="tab">
  	<h2>
  	<xsl:value-of select="@name"/>
  	</h2>
  	<xsl:for-each select="Field">
  		<xsl:call-template name="Field">
  		</xsl:call-template>
  	</xsl:for-each>
  	</div>
  </xsl:template>
  
  <xsl:template name="Field">
    <div class="helpentry">
      <xsl:for-each select="Param">
        <a name="{@name}"/>
      </xsl:for-each>           
      <h3>
        <xsl:choose>
          <xsl:when test="Title">
            <xsl:value-of select="Title"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:for-each select="Param">
              <xsl:value-of select="@name"/>
              <xsl:if test="position()!=last()">
                <xsl:text> | </xsl:text>
              </xsl:if>
            </xsl:for-each>
          </xsl:otherwise>
        </xsl:choose>
      </h3>
      <div class="helpentryContent">
        <xsl:copy-of select="HtmlText/*"/>
        <xsl:if test="Title">
          <p align="right">
            <xsl:for-each select="Param">
              <xsl:value-of select="@name"/>
              <xsl:if test="position()!=last()">
                <xsl:text> | </xsl:text>
              </xsl:if>
            </xsl:for-each>
          </p>
        </xsl:if>
      </div>
    </div>
  </xsl:template>

</xsl:stylesheet>
