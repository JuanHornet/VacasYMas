package com.example.vacasymas.network.services;

import com.example.vacasymas.data.models.NotaAnimal;
import com.google.gson.JsonArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NotaAnimalService {

    @Headers({
            "Prefer: resolution=merge-duplicates"
    })
    @POST("notas_animales")
    Call<Void> upsertNotasAnimales(@Body JsonArray body);

    @GET("notas_animales")
    Call<List<NotaAnimal>> getNotasDesdeFecha(
            @Query("select") String select,
            @Query("fecha_actualizacion") String fechaFiltro,
            @Query("order") String order
    );
}
