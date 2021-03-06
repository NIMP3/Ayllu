package com.qhapaq.nan.ayllu.domain;

import com.qhapaq.nan.ayllu.domain.factor.Factor;
import com.qhapaq.nan.ayllu.domain.variable.Variable;
import com.qhapaq.nan.ayllu.io.model.JsonKeys;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Categoria {
    @SerializedName(JsonKeys.FACTORES_ARRAY)
    ArrayList<Factor> factores;
    @SerializedName(JsonKeys.VARIABLES_ARRAY)
    ArrayList<Variable> variables;

    public Categoria(ArrayList<Factor> factores, ArrayList<Variable> variables) {
        this.factores = factores;
        this.variables = variables;
    }

    public ArrayList<Factor> getFactores() {
        return factores;
    }

    public void setFactores(ArrayList<Factor> factores) {
        this.factores = factores;
    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }

    public void setVariables(ArrayList<Variable> variables) {
        this.variables = variables;
    }
}
