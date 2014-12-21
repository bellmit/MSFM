<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fix="http://www.cboe.com/fix/xslt"
                >

<!-- @author Dmitry Volpyansky

     must be built with JDK 1.4.1 and Saxon XSLT version 7.4 from http://saxon.sourceforge.net/

     classpath needs to include /client/cfix/generator/saxon7.jar and /client/cfix/generator/saxon7_jdom.jar

     library of utilities to use for other stylesheets
-->

<xsl:function name="fix:removePrefix">
   <xsl:param name="str"/>
   <xsl:param name="prefix"/>
   <xsl:result select="if (substring($str, 1, string-length($prefix)) = $prefix)
                         then substring($str, string-length($prefix) + 1, string-length($str) - string-length($prefix))
                         else $str"/>
</xsl:function>

<xsl:function name="fix:max">
   <xsl:param name="a"/>
   <xsl:param name="b"/>
   <xsl:result select="if (number($a) > number($b)) then number($a) else number($b)"/>
</xsl:function>

<xsl:function name="fix:rightPad">
   <xsl:param name="str"/>
   <xsl:param name="maximum"/>
   <xsl:result select="if (string-length($str) &lt; $maximum)
                         then substring(concat($str,
'                                                                                                                                                                                                                                                                                                                                                     '
                                ), 1, $maximum)
                         else $str"
                         />
</xsl:function>

<xsl:function name="fix:buildFixFieldName">
   <xsl:param name="str"/>
   <xsl:result select="concat('Fix', $str, 'Field')"
               />
</xsl:function>

<xsl:function name="fix:buildFixMessageName">
   <xsl:param name="str"/>
   <xsl:result select="concat('Fix', $str, 'Message')"
               />
</xsl:function>

<xsl:function name="fix:buildFixMessageNameWithParen">
   <xsl:param name="str"/>
   <xsl:result select="concat(fix:buildFixMessageName($str), '()')"
               />
</xsl:function>

<xsl:function name="fix:buildFixFieldNameDotTagID">
   <xsl:param name="str"/>
   <xsl:result select="concat(fix:buildFixFieldName($str), '.TagID')"
               />
</xsl:function>

<xsl:function name="fix:findLongestString">
   <xsl:param name="nodeset"/>
   <xsl:variable name="theLongest">
     <xsl:call-template name="fix:longestLengthString">
       <xsl:with-param name="nodeset" select="$nodeset"/>
     </xsl:call-template>
   </xsl:variable>
   <xsl:result select="$theLongest"/>
</xsl:function>

<!--
    <xsl:variable name="capitalizedFieldName">
        <xsl:call-template name="fix:FixHelper_lowercaseFirstChar">
            <xsl:with-param name="string" select="@name"/>
        </xsl:call-template>
    </xsl:variable>
    public <xsl:value-of select="@name"/><xsl:text> </xsl:text><xsl:value-of select="$capitalizedFieldName"/>;<xsl:text/>

<xsl:message>
s=<xsl:value-of select="$string"/> p=<xsl:value-of select="$prefix"/> len=<xsl:value-of select="string-length($prefix)"/> sub=<xsl:value-of select="substring($string, 1, string-length($prefix))"/> fin=<xsl:value-of select="substring($string, string-length($prefix) + 1, string-length($string) - string-length($prefix))"/>
</xsl:message>

<xsl:message>paddedDescription='<xsl:value-of select="$paddedDescription"/>'</xsl:message>

<xsl:template match="fix/fields/field">
  <xsl:variable name="theLongest">
    <xsl:call-template name="fix:longestLengthString">
      <xsl:with-param name="nodeset" select="value/@description"/>
    </xsl:call-template>
  </xsl:variable>
</xsl:template>

-->

<xsl:template name="fix:FixHelper_lowercaseFirstChar">
   <xsl:param name="string"/>
   <xsl:value-of select="concat(
                                translate(substring($string, 1, 1),
                                    'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
                                    'abcdefghijklmnopqrstuvwxyz'),
                                substring($string, 2, string-length($string) - 1))"
                                />
</xsl:template>

<xsl:template name="fix:FixHelper_removePrefix">
   <xsl:param name="string"/>
   <xsl:param name="prefix"/>
   <xsl:value-of select="if (substring($string, 1, string-length($prefix)) = $prefix)
                         then substring($string, string-length($prefix) + 1, string-length($string) - string-length($prefix))
                         else $string"
                         />
</xsl:template>

<xsl:template name="fix:longestLengthString">
    <xsl:param name="nodeset"/>
    <xsl:param name="longest" select="0"/>

    <xsl:choose>
    <xsl:when test="$nodeset">
      <xsl:choose>
        <xsl:when test="string-length($nodeset[1]) > $longest">
          <xsl:call-template name="fix:longestLengthString">
             <xsl:with-param name="nodeset" select="$nodeset[position() > 1]"/>
             <xsl:with-param name="longest" select="string-length($nodeset[1])"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="fix:longestLengthString">
             <xsl:with-param name="nodeset" select="$nodeset[position() > 1]"/>
             <xsl:with-param name="longest" select="$longest"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
        <xsl:value-of select="$longest"/>
    </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:function name="fix:splitIntoChars">
   <xsl:param name="string"/>
   <xsl:variable name="string_split">
     <xsl:call-template name="fix:splitIntoChars">
       <xsl:with-param name="string" select="$string"/>
     </xsl:call-template>
   </xsl:variable>
   <xsl:result select="$string_split"/>
</xsl:function>

<xsl:template name="fix:splitIntoChars">
  <xsl:param name="string"/>
  <xsl:if test="$string">
    <xsl:choose>
    <xsl:when test="string-length($string) > 1">'<xsl:value-of select="substring($string, 1, 1)"/>',<xsl:text/></xsl:when>
    <xsl:otherwise>'<xsl:value-of select="substring($string, 1, 1)"/>'<xsl:text/></xsl:otherwise>
    </xsl:choose>
    <xsl:call-template name="fix:splitIntoChars">
      <xsl:with-param name="string" select="substring($string, 2)"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>
