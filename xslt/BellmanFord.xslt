<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        \documentclass[crop,tikz, border=5pt]{beamer}
        <xsl:call-template name="import"/>
        \begin{document}
        <xsl:variable name="root" select="SingleSourceShortestPath"/>
        <xsl:call-template name="create_graph">
            <xsl:with-param name="root" select="$root"/>
        </xsl:call-template>

        <xsl:for-each select="$root/iterations/shortestPath">
            <xsl:variable name="shortestPathIndex" select="position()"/>
            <xsl:variable name="queuedElements">
                <xsl:for-each select="$root/iterations/shortestPath[position()&lt;=($shortestPathIndex)]">
                    <xsl:value-of select="concat('|',@queuedElement,'|')"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:call-template name="create_graph">
                <xsl:with-param name="queuedElements" select="$queuedElements"/>
                <xsl:with-param name="pathSet" select="path"/>
                <xsl:with-param name="shortestPathIndex" select="$shortestPathIndex"/>
                <xsl:with-param name="root" select="$root"/>
            </xsl:call-template>
        </xsl:for-each>

        \end{document}
    </xsl:template>



    <xsl:template name="create_graph">
        <xsl:param name="root"/>
        <xsl:param name="queuedElements" select="''"/>
        <xsl:param name="shortestPathIndex" select="-1"/>
        <xsl:param name="shortestPathSet" select="$root/iterations/shortestPath[position()&lt;=($shortestPathIndex)]"/>
        <xsl:param name="pathSet" select="node()"/>
        <xsl:param name="print_label" select="true()"/>
        <xsl:variable name="type">
            <xsl:choose>
                <xsl:when test="$root/graphml/graph/@edgedefault='undirected'">-</xsl:when>
                <xsl:otherwise>-></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        \begin{tikzpicture}[shorten >=1pt, auto, node distance=3cm, ultra thick,
        node_style/.style={circle,draw=blue,fill=blue!20!,font=\sffamily\Large\bfseries},
        edge_node_style/.style={draw=none,fill=none,midway,sloped},
        selected_node_style/.style={circle,draw=blue,fill=yellow!20!,font=\sffamily\Large\bfseries},
        edge_style/.style={draw=black, ultra thick,<xsl:value-of select="$type"/>},
        selected_edge_style/.style={draw=yellow, ultra thick,<xsl:value-of select="$type"/>}]
        \tikzset{quadratic bezier/.style={ to path={(\tikztostart) .. controls($#1!1/3!(\tikztostart)$) and ($#1!1/3!(\tikztotarget)$).. (\tikztotarget)}}}
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
        <xsl:param name="print_label" select="true()"/>
        <xsl:param name="distance_from_source" select="$pathSet[position()=$pos]/@distance"/>
        <xsl:variable name="style">node_style</xsl:variable>
<!--            <xsl:choose>-->
<!--                <xsl:when test="contains($queuedElements,concat('|',@label,'|'))">selected_node_style</xsl:when>-->
<!--                <xsl:otherwise>node_style</xsl:otherwise>-->
<!--            </xsl:choose>-->

<!--        </xsl:variable>-->
        <xsl:variable name="distLabel">
            <xsl:if test="$print_label = true()">,label={[text=blue]10:$<xsl:value-of select="$distance_from_source"/>$}</xsl:if>
        </xsl:variable>
        \node [<xsl:value-of select="concat($style,$distLabel)"/>](<xsl:value-of select="@id"/>) at (<xsl:value-of select="concat(@x,',',@y)"/>) {<xsl:value-of select="@label"/>};</xsl:template>

    <xsl:template name = "create_edge" >
        <xsl:param name="label"/>
        <xsl:choose>
            <xsl:when test="boolean(controlpoint)">
                <xsl:choose>
                    <xsl:when test="count(controlpoint)=2">
                        <xsl:variable name="cp1" select="controlpoint[position()=1]"/>
                        <xsl:variable name="cp2" select="controlpoint[position()=2]"/>
                        <xsl:value-of select="concat('\draw [',$label,'] (',@source,') .. controls (',$cp1/@x,',',$cp1/@y,') and (',$cp2/@x,',',$cp2/@y,') .. node [edge_node_style] {$',@weight,'$} (',@target)"/>);
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:variable name="crtl" select="concat('crtl',position())"/>
                        <xsl:value-of select="concat('\coordinate (',$crtl,')  at (',controlpoint/@x,',',controlpoint/@y)"/>);
                        <xsl:value-of select="concat('\draw [',$label,'] (',@source,') .. controls($(',$crtl,')!1/3!(',@source,')$) and ($(',$crtl,')!1/3!(',@target,')$).. node [edge_node_style] {$',@weight,'$} (',@target)"/>);
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="connection">
                    <xsl:choose>
                        <xsl:when test="@style!=''"><xsl:value-of select="concat(@style,',')"/>edge label=<xsl:value-of select="@weight"/></xsl:when>
                        <xsl:otherwise>edge label=<xsl:value-of select="@weight"/></xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:value-of select="concat('\draw [',$label,'] (',@source,') to[',$connection,'] (',@target)"/>);
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

    <xsl:template name = "import" >
        \usepackage{tikz}
        \usetikzlibrary{calc}
        \usetheme{default}
        \usefonttheme{professionalfonts}
        \setbeamertemplate{navigation symbols}{}
        \setbeamerfont{frametitle}{series=\bfseries}
        \tikzset{ n/.style = { circle % node
        , very thick
        , draw
        , fill = yellow
        , minimum size = 4mm
        }
        , d/.style = { rectangle % distance
        , minimum size = 4mm
        , font = \color{blue!50!black}\LARGE
        }
        , v/.style = { rectangle % via vertex
        , minimum size = 4mm
        , font = \color{green!50!black}
        }
        }
        \newcommand{\oo}{\ensuremath\infty}
        \def\dst(#1,#2)#3{% distance
        \pgfmathtruncatemacro{\x}{3*#2}
        \pgfmathtruncatemacro{\y}{(-2)*#1+12}
        \node[d] at (\x,\y) {#3};
        }
        \def\via(#1,#2)#3{% via vertex
        \pgfmathtruncatemacro{\x}{3*#2+1}
        \pgfmathtruncatemacro{\y}{(-2)*#1+12}
        \node[v] at (\x,\y) {#3};
        }
    </xsl:template>

</xsl:stylesheet>

