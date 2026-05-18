package com.example.vacasymas.network.services;

import com.example.vacasymas.data.models.CrotalDisponible;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CrotalDisponibleService {

    @GET("crotales_disponibles")
    Call<List<CrotalDisponible>> getCrotalesDesdeFecha(
            @Query("select") String select,
            @Query("fecha_actualizacion") String fechaFiltro,
            @Query("order") String order
    );

    @GET("crotales_disponibles")
    Call<List<CrotalDisponible>> getCrotales(
            @Header("Range") String range,
            @Header("Range-Unit") String rangeUnit,
            @Query("select") String select,
            @Query("order") String order
    );

    @Headers({
            "Prefer: resolution=merge-duplicates,return=minimal"
    })
    @POST("crotales_disponibles")
    Call<Void> upsertCrotales(
            @Query("on_conflict") String onConflict,
            @Body List<CrotalDisponible> body
    );
}
