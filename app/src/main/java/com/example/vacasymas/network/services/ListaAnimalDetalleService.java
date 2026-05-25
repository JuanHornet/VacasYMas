package com.example.vacasymas.network.services;

import com.example.vacasymas.data.models.AnimalEnLista;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ListaAnimalDetalleService {

    @GET("lista_animales_detalle")
    Call<List<AnimalEnLista>> getDetallesDesdeFecha(
            @Query("select") String select,
            @Query("fecha_actualizacion") String fechaFiltro,
            @Query("order") String order
    );

    @POST("lista_animales_detalle")
    Call<Void> upsertDetalles(
            @Query("on_conflict") String onConflict,
            @Body List<AnimalEnLista> detalles,
            @Header("Prefer") String prefer
    );
}