<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output omit-xml-declaration="yes" indent="yes"/>
    <xsl:template match="/cyk">
        \documentclass{beamer}
        <xsl:call-template name="init"/>
        \begin{document}
            <xsl:call-template name="grammar"/>
        <xsl:for-each select="iterations/iteration">
            \begin{frame}
            \frametitle{CYK Algorithm: Iteration <xsl:value-of select="position()-1"/>}
                \begin{center}
                    <xsl:call-template name="table"/>
                \end{center}
            \end{frame}
        </xsl:for-each>
        <xsl:for-each select="encodingTrees/encodingTree">
            \begin{frame}
            \frametitle{CYK Algorithm: Encoding <xsl:value-of select="@encoding"/>}
                \begin{center}
                    <xsl:call-template name="encodings"/>
                \end{center}
            \end{frame}
        </xsl:for-each>
        \end{document}
    </xsl:template>


    <xsl:template name="init">
        \usepackage{mathtools}
        \usepackage[linguistics]{forest}
        \usefonttheme{professionalfonts}
        \setbeamertemplate{navigation symbols}{}
        \setbeamerfont{frametitle}{series=\bfseries}
        \DeclarePairedDelimiter\set\{\}
    </xsl:template>
                <xsl:template name="table">
                    <xsl:variable name="cols">
                        <xsl:for-each select="0 to count(row)">c|</xsl:for-each>
                    </xsl:variable>
                    <xsl:variable name="header">
                        <xsl:for-each select="1 to count(row)"> &amp; <xsl:value-of select="concat('k=',position())"/></xsl:for-each> \\</xsl:variable>
                    \begin{tabular}{<xsl:value-of select="$cols"/>}
                    <xsl:value-of select="$header"/>
                    \hline
                    <xsl:for-each select="row">i=<xsl:value-of select="position()"/> &amp; <xsl:value-of select="replace(replace(replace(replace(.,';',' &amp; '),'\[\]','\\emptyset'),'\[','\\set{'),'\]','}')"/> \\
                    \hline
                    </xsl:for-each>
                    \end{tabular}
                </xsl:template>

                <xsl:template name="grammar">
                    \begin{frame}
                    \frametitle{CYK Algorithm : Grammar}
                    \begin{center}
                        We consider the CFG(V,T,P,S) with \\ T = \set{<xsl:value-of select="cfg/terminals"/>}, V=\set{<xsl:value-of select="cfg/variables"/>} \\ and the productions: \\
                            <xsl:for-each select="cfg/projectionRules/rule">
                                <xsl:value-of select="replace(replace(.,'\|',' \\mid '),'-&gt;',' \\Rightarrow ')"/>\\
                            </xsl:for-each>
                    \end{center}
                    \end{frame}
                </xsl:template>

                <xsl:template name="encodings">
                    \begin{forest}
                    <xsl:value-of select="."/>
                    \end{forest}
                </xsl:template>
</xsl:stylesheet>

