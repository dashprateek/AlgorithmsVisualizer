<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
    \documentclass{beamer}
        <xsl:call-template name="import"/>
        \begin{document}
        <xsl:variable name="root" select="/GraphTraversal"/>
        <xsl:variable name="algorithm">
            <xsl:choose>
                <xsl:when test="$root/@algorithm='bfs'">Breadth-First Traversal</xsl:when>
                <xsl:otherwise>Depth-First Traversal</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        \begin{frame}
        \frametitle{<xsl:value-of select="$algorithm"/> : Input}
        \begin{center}
        <xsl:call-template name="create_graph">
            <xsl:with-param name="root" select="$root"/>
        </xsl:call-template>
        \end{center}
        \end{frame}
        <xsl:for-each select="$root/iterations/iteration">
            <xsl:variable name="title">
                <xsl:choose>
                    <xsl:when test="$root/@algorithm='bfs'">Queue:</xsl:when>
                    <xsl:otherwise>Stack:</xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            \begin{frame}
            \frametitle{<xsl:value-of select="concat($algorithm,' Iteration:',position(),' ',$title,'[',@list,']')"/>}
            <xsl:call-template name="create_graph">
                <xsl:with-param name="queuedElements" select="@visited"/>
                <xsl:with-param name="root" select="$root"/>
                <xsl:with-param name="print_label" select="false"/>
            </xsl:call-template>
            \end{frame}
        </xsl:for-each>
    \end{document}
    </xsl:template>


    <xsl:template name="create_graph">
        <xsl:param name="root"/>
        <xsl:param name="queuedElements" select="''"/>
        <xsl:param name="shortestPathIndex" select="''"/>
        <xsl:param name="shortestPathSet" select="$root/iterations/shortestPath[position()&lt;=($shortestPathIndex)]"/>
        <xsl:param name="pathSet" select="node()"/>
        <xsl:param name="print_label" select="true"/>
        <xsl:variable name="type">
            <xsl:choose>
                <xsl:when test="$root/graphml/graph/@edgedefault='undirected'">-</xsl:when>
                <xsl:otherwise>-></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        \begin{tikzpicture}[shorten >=1pt, auto, node distance=3cm, ultra thick,
        node_style/.style={circle,draw=blue,fill=blue!20!,font=\sffamily\Large\bfseries},
        selected_node_style/.style={circle,draw=blue,fill=yellow!20!,font=\sffamily\Large\bfseries},
        edge_style/.style={draw=black, ultra thick,<xsl:value-of select="$type"/>},
        selected_edge_style/.style={draw=yellow, ultra thick,<xsl:value-of select="$type"/>}]
        <xsl:for-each select="$root/graphml/graph/node">
            <xsl:call-template name="create_node">
                <xsl:with-param name="queuedElements" select="$queuedElements"/>
                <xsl:with-param name="pathSet" select="$pathSet"/>
                <xsl:with-param name="print_label" select="$print_label"/>
            </xsl:call-template>
        </xsl:for-each>
        <xsl:for-each select="$root/graphml/graph/edge">
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
        <xsl:param name="print_label" select="true"/>
        <xsl:param name="distance_from_source" select="$pathSet[position()=$pos]/@distance"/>
        <xsl:variable name="style">
            <xsl:choose>
                <xsl:when test="contains($queuedElements,concat('|',@label,'|'))">selected_node_style</xsl:when>
                <xsl:otherwise>node_style</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="distLabel">
                <xsl:if test="$print_label = true">,label={[text=blue]10:$<xsl:value-of select="$distance_from_source"/>$}</xsl:if>
        </xsl:variable>
        \node [<xsl:value-of select="concat($style,$distLabel)"/>](<xsl:value-of select="@id"/>) at (<xsl:value-of select="@x"/>,<xsl:value-of select="@y"/>) {<xsl:value-of select="@label"/>};</xsl:template>

    <xsl:template name = "create_edge" >
        <xsl:param name="label"/>
        \draw [<xsl:value-of select="$label"/>] (<xsl:value-of select="@source"/>) edge node{<xsl:value-of select="@weight"/>} (<xsl:value-of select="@target"/>);</xsl:template>

    <xsl:template name = "import" >
        \usepackage{pgf}
        \usepackage{tikz}
        \usepackage{amsmath, amssymb}
        \usetikzlibrary{arrows.meta}
        \usetikzlibrary{arrows}
        \usetikzlibrary{calc}
        \usetikzlibrary{shapes}
        \usepackage[utf8]{inputenc}
        \usetheme{default}
        \usefonttheme{professionalfonts}
        \setbeamertemplate{navigation symbols}{}
        \setbeamerfont{frametitle}{series=\bfseries}
    </xsl:template>
</xsl:stylesheet>

