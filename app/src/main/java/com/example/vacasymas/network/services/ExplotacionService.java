package com.example.vacasymas.network.services;

import com.example.vacasymas.data.models.Explotacion;
import com.google.gson.JsonArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ExplotacionService {

    @GET("explotaciones")
    Call<List<Explotacion>> getExplotacionesPorUsuario(
            @Query("select") String select,
            @Query("id_usuario") String idUsuario,
            @Query("order") String order
    );

    @GET("explotaciones")
    Call<List<Explotacion>> getExplotacionesPorUsuarioDesdeFecha(
            @Query("select") String select,
            @Query("id_usuario") String idUsuario,
            @Query("fecha_actualizacion") String fechaActualizacion,
            @Query("order") String order
    );

    @Headers({
            "Prefer: resolution=merge-duplicates"
    })
    @POST("explotaciones")
    Call<Void> upsertExplotaciones(@Body JsonArray body);
}
