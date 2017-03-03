package com.example.edwin.ayllu;

import com.example.edwin.ayllu.domain.Factor;
import com.example.edwin.ayllu.domain.Monitoreo;
import com.example.edwin.ayllu.domain.MonitoreoGeneral;
import com.example.edwin.ayllu.domain.Pais;
import com.example.edwin.ayllu.domain.PuntoCritico;
import com.example.edwin.ayllu.domain.ResumenMonitor;
import com.example.edwin.ayllu.domain.Usuario;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RestClient {

    String BASE_URL="http://138.68.40.165/webservice/";

    @GET("resumenMonitor/{ide}")
    Call<ArrayList<ResumenMonitor>> resumenMonitor(@Path("ide") String ide);

    @GET("filtrarMonitoreosConFecha/{p1}/{p2}/{p3}/{p4}/{p5}/{p6}/{fi}/{ff}/{fac}/{var}")
    Call<ArrayList<PuntoCritico>> getFiltro(@Path("p1") String p1,
                                            @Path("p2") String p2,
                                            @Path("p3") String p3,
                                            @Path("p4") String p4,
                                            @Path("p5") String p5,
                                            @Path("p6") String p6,
                                            @Path("fi") String fi,
                                            @Path("ff") String ff,
                                            @Path("fac") String fac,
                                            @Path("var") String var);
    @GET ("descripcionPC/{paf}/{fecha}")
    Call<ArrayList<MonitoreoGeneral>> informacion (@Path("paf") String paf,
                                                   @Path("fecha") String fec);

    @GET("login/{ide_usu}/{pw_usu}")
    Call<ArrayList<Usuario>> getUsuario(@Path("ide_usu") String identificacion_usu,
                                        @Path("pw_usu") String contrasena_usu);

    @GET("add/{ide}/{nom}/{ape}/{tipo}/{con}/{pais}")
    Call<ArrayList<String>> addUsuario(@Path("ide") String ide,
                                       @Path("nom") String nom,
                                       @Path("ape") String ape,
                                       @Path("tipo") String tipo,
                                       @Path("con") String con,
                                       @Path("pais") String pais);

    @GET("edit/{ide}/{nom}/{ape}/{con}/{clave}")
    Call<ArrayList<String>> editUsuario(@Path("ide") String ide,
                                       @Path("nom") String nom,
                                       @Path("ape") String ape,
                                       @Path("con") String con,
                                       @Path("clave") String pais);
    @GET("getMonitores/{pais}")
    Call<ArrayList<Usuario>> getUsuarios(@Path("pais") String pais);

    @GET("update/{ide}")
    Call<ArrayList<Usuario>>update(@Path("ide") String ide);

    @GET("deshabilitar/{ide}")
    Call<ArrayList<String>> deleteUsuario(@Path("ide") String ide);

    @GET("monitoreos/{area}")
    Call <ArrayList<Monitoreo>> monitoreos (@Path("area") String area);

    @GET ("addRespuesta/{pa}/{fm}/{eva}/{per}/{tie}/{pre}/{rec}/{con}/{mon}")
    Call<ArrayList<String>> addRespuesta (@Path("pa") String pa,
                                          @Path("fm") String fm,
                                          @Path("eva") String eva,
                                          @Path("per") String per,
                                          @Path("tie") String tie,
                                          @Path("pre") String pre,
                                          @Path("rec") String rec,
                                          @Path("con") String con,
                                          @Path("mon") String mon);


    public static final Retrofit retrofit = new Retrofit.Builder().baseUrl(RestClient.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build();
}
