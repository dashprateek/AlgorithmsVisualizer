<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/graphml">
\documentclass[crop,tikz, border=5pt]{standalone}
\usepackage{pgf}
\usepackage{tikz}
\usepackage{amsmath, amssymb}
\usetikzlibrary{arrows.meta}
\usetikzlibrary{arrows}
\usetikzlibrary{calc}
\usetikzlibrary{shapes}
\usepackage[utf8]{inputenc}
\begin{document}
    <xsl:for-each select="graph">
    \begin{tikzpicture}[shorten >=1pt, auto, node distance=3cm, ultra thick,
    node_style/.style={circle,draw=blue,fill=blue!20!,font=\sffamily\Large\bfseries},
    selected_node_style/.style={circle,draw=blue,fill=yellow!20!,font=\sffamily\Large\bfseries},
    edge_style/.style={draw=black, ultra thick, <xsl:choose><xsl:when test="@edgedefault='undirected'">-</xsl:when><xsl:otherwise>-></xsl:otherwise></xsl:choose>},
    selected_edge_style/.style={draw=yellow, ultra thick,<xsl:choose><xsl:when test="@edgedefault='undirected'">-</xsl:when><xsl:otherwise>-></xsl:otherwise></xsl:choose>}]
        <xsl:for-each select="node">
        \node [<xsl:value-of select="@style"/>
            <xsl:choose>
            <xsl:when test="@print_label = true">
            ,label={[text=blue]10:$<xsl:value-of select="@distance_from_source"/>$}
            </xsl:when>
            </xsl:choose>] (<xsl:value-of select="@id"/>) at (<xsl:value-of select="@x"/>,<xsl:value-of select="@y"/>) {<xsl:value-of select="@label"/>};
        </xsl:for-each>
        <xsl:for-each select="edge">
        \draw [<xsl:value-of select="@style"/>] (<xsl:value-of select="@source"/>) edge node{<xsl:value-of select="@weight"/>} (<xsl:value-of select="@target"/>);
        </xsl:for-each>
    \end{tikzpicture}
    </xsl:for-each>
\end{document}
</xsl:template>
</xsl:stylesheet>

