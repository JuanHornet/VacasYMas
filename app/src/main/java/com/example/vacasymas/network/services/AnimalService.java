package com.example.vacasymas.network.services;

import com.example.vacasymas.data.models.Animal;
import com.google.gson.JsonArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AnimalService {

    @GET("animales")
    Call<List<Animal>> getAnimales(
            @Header("Range") String range,
            @Header("Range-Unit") String rangeUnit,
            @Query("select") String select,
            @Query("order") String order
    );

    @Headers({
            "Prefer: resolution=merge-duplicates,return=representation"
    })
    @POST("animales")
    Call<Void> upsertAnimales(
            @Query("on_conflict") String onConflict,
            @Body JsonArray body);

    @GET("animales")
    Call<List<Animal>> getAnimalesDesdeFecha(
            @Query("select") String select,
            @Query("fecha_actualizacion") String fechaActualizacionFilter,
            @Query("order") String order
    );

    @GET("animales")
    Call<List<Animal>> getAnimalesDesdeFecha(
            @Header("Range") String range,
            @Header("Range-Unit") String rangeUnit,
            @Query("select") String select,
            @Query("fecha_actualizacion") String fechaFiltro,
            @Query("order") String order
    );

    @Headers({
            "Prefer: return=representation"
    })
    @PATCH("animales")
    Call<Void> actualizarAnimalPorId(
            @Query("id") String idFiltro,
            @Body com.google.gson.JsonObject body
    );
}
