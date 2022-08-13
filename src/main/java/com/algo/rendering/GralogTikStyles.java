/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package com.algo.rendering;

import java.io.Serializable;

/**
 *
 */
public enum GralogTikStyles implements Serializable {
    NODE_STYLE("node_style"), SELECTED_NODE_STYLE("selected_node_style"), EDGE_STYLE("edge_style"), SELECTED_EDGE_STYLE("selected_edge_style");
    private final String style;

    private GralogTikStyles(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }

    public static GralogTikStyles valueOfStyle(String style){
        for (GralogTikStyles tikStyle : values()) {
            if (tikStyle.getStyle().equals(style)) {
                return tikStyle;
            }
        }
        return null;
    }
}