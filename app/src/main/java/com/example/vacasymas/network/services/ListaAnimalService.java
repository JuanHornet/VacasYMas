package com.example.vacasymas.network.services;

import com.example.vacasymas.data.models.ListaAnimal;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ListaAnimalService {

    @GET("listas_animales")
    Call<List<ListaAnimal>> getListasDesdeFecha(
            @Query("select") String select,
            @Query("fecha_actualizacion") String fechaFiltro,
            @Query("order") String order
    );

    @POST("listas_animales")
    Call<Void> upsertListas(
            @Query("on_conflict") String onConflict,
            @Body List<ListaAnimal> listas,
            @Header("Prefer") String prefer
    );

    @PATCH("listas_animales")
    Call<Void> updateLista(
            @Query("id") String idFiltro,
            @Body ListaAnimal lista,
            @Header("Prefer") String prefer
    );
}