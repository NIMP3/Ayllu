package com.qhapaq.nan.ayllu.io.model;

import com.qhapaq.nan.ayllu.domain.usuario.Usuario;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UsuarioResponse {
    @SerializedName(JsonKeys.USUARIOS_RESULTS)
    UsuarioResponse.UsuarioResult result;

    public void setCategorias(ArrayList<Usuario> usuarios){
        this.result.usuarios = usuarios;
    }

    public ArrayList<Usuario> getUsuarios(){
        return result.usuarios;
    }

    private class UsuarioResult {
        ArrayList<Usuario> usuarios;
    }
}
