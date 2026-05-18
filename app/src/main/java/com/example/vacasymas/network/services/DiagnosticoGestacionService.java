package com.example.vacasymas.network.services;

import com.example.vacasymas.data.models.DiagnosticoGestacion;
import com.google.gson.JsonArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DiagnosticoGestacionService {

    @GET("diagnosticos_gestacion")
    Call<List<DiagnosticoGestacion>> getDiagnosticosDesdeFecha(
            @Query("select") String select,
            @Query("fecha_actualizacion") String fechaFiltro,
            @Query("order") String order
    );

    @Headers({
            "Prefer: resolution=merge-duplicates",
            "Prefer: return=minimal"
    })
    @POST("diagnosticos_gestacion?on_conflict=id_animal,fecha")
    Call<Void> upsertDiagnosticos(@Body JsonArray body);
}
