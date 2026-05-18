package com.example.vacasymas.network.services;

import com.example.vacasymas.data.models.PesoAnimal;
import com.google.gson.JsonArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PesoAnimalService {

    @GET("pesos_animales")
    Call<List<PesoAnimal>> getPesosDesdeFecha(
            @Query("select") String select,
            @Query("fecha_actualizacion") String fechaFiltro,
            @Query("order") String order
    );

    @Headers({
            "Prefer: resolution=merge-duplicates",
            "Prefer: return=minimal"
    })
    @POST("pesos_animales?on_conflict=id_animal,fecha")
    Call<Void> upsertPesos(@Body JsonArray body);
}
