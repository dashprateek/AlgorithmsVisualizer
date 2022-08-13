<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        \documentclass[crop,tikz, border=5pt]{standalone}
        <xsl:call-template name="import"/>
        \begin{document}

        <xsl:call-template name="create_graph"/>

        <xsl:for-each select="/SingleSourceShortestPath/iterations/shortestPath">
            <xsl:variable name="shortestPathIndex" select="position()"/>
            <xsl:variable name="queuedElements">
                <xsl:for-each select="/SingleSourceShortestPath/iterations/shortestPath[position()&lt;=($shortestPathIndex)]">
                    <xsl:value-of select="concat('|',@queuedElement,'|')"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:call-template name="create_graph">
                <xsl:with-param name="queuedElements" select="$queuedElements"/>
                <xsl:with-param name="pathSet" select="path"/>
                <xsl:with-param name="shortestPathIndex" select="$shortestPathIndex"/>
            </xsl:call-template>
        </xsl:for-each>

        \end{document}
    </xsl:template>


    <xsl:template name="create_graph" match="/SingleSourceShortestPath/graphml/graph">
        <xsl:param name="queuedElements" select="''"/>
        <xsl:param name="shortestPathIndex" select="''"/>
        <xsl:param name="shortestPathSet" select="/SingleSourceShortestPath/iterations/shortestPath[position()&lt;=number($shortestPathIndex)]"/>
        <xsl:param name="pathSet" select="node()"/>
        \begin{tikzpicture}[shorten >=1pt, auto, node distance=3cm, ultra thick,
        node_style/.style={circle,draw=blue,fill=blue!20!,font=\sffamily\Large\bfseries},
        selected_node_style/.style={circle,draw=blue,fill=yellow!20!,font=\sffamily\Large\bfseries},
        edge_style/.style={draw=black, ultra thick,<xsl:choose><xsl:when test="@edgedefault='undirected'">-</xsl:when><xsl:otherwise>-></xsl:otherwise></xsl:choose>},
        selected_edge_style/.style={draw=yellow, ultra thick,<xsl:choose><xsl:when test="@edgedefault='undirected'">-</xsl:when><xsl:otherwise>-></xsl:otherwise></xsl:choose>}]
        <xsl:for-each select="/SingleSourceShortestPath/graphml/graph/node">
            <xsl:call-template name="create_node">
                <xsl:with-param name="queuedElements" select="$queuedElements"/>
                <xsl:with-param name="pathSet" select="$pathSet"/>
            </xsl:call-template>
        </xsl:for-each>
        <xsl:for-each select="/SingleSourceShortestPath/graphml/graph/edge">
            <xsl:variable name="targetNodeIdx" select="number(substring(@target, string-length(@target), 1)) -1"/>
            <xsl:variable name="source"
                          select="concat('n',string(number($shortestPathSet[@queuedElement=$targetNodeIdx]/path[position()=$targetNodeIdx+1]/@parent)+1))"/>
            <xsl:variable name="label">
                <xsl:choose>
                    <xsl:when test="@source=$source">selected_edge_style</xsl:when>
                    <xsl:otherwise>edge_style</xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:call-template name="create_edge">
                <xsl:with-param name="label" select="$label"/>
            </xsl:call-template>
        </xsl:for-each>
        \end{tikzpicture}
    </xsl:template>

    <xsl:template name = "create_node" >
        <xsl:param name="queuedElements"/>
        <xsl:param name="pathSet"/>
        <xsl:param name="pos" select="position()"/>
        <xsl:param name="print_label" select="boolean(true)"/>
        <xsl:param name="distance_from_source" select="$pathSet[position()=$pos]/@distance"/>
        \node [<xsl:choose><xsl:when test="contains($queuedElements,concat('|',@label,'|'))">selected_node_style</xsl:when><xsl:otherwise>node_style</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="$print_label = boolean(true)">,label={[text=blue]10:$<xsl:value-of select="$distance_from_source"/>$}</xsl:when></xsl:choose>](<xsl:value-of select="@id"/>) at (<xsl:value-of select="@x"/>,<xsl:value-of select="@y"/>) {<xsl:value-of select="@label"/>};
    </xsl:template>

    <xsl:template name = "create_edge" >
        <xsl:param name="label"/>
        \draw [<xsl:value-of select="$label"/>] (<xsl:value-of select="@source"/>) edge node{<xsl:value-of select="@weight"/>} (<xsl:value-of select="@target"/>);
    </xsl:template>

    <xsl:template name = "import" >
        \usepackage{pgf}
        \usepackage{tikz}
        \usepackage{amsmath, amssymb}
        \usetikzlibrary{arrows.meta}
        \usetikzlibrary{arrows}
        \usetikzlibrary{calc}
        \usetikzlibrary{shapes}
        \usepackage[utf8]{inputenc}
    </xsl:template>
</xsl:stylesheet>

