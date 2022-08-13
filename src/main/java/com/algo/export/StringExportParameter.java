/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package com.algo.export;

/**
 *
 */
public class StringExportParameter extends ExportFilterParameters {

    public String parameter;

    public StringExportParameter() {
        this.parameter = "";
    }

    public StringExportParameter(String parameter) {
        this.parameter = parameter;
    }

}
