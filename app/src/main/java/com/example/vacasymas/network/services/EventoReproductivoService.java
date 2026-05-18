package com.example.vacasymas.network.services;

import com.example.vacasymas.data.models.EventoReproductivo;
import com.google.gson.JsonArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EventoReproductivoService {

    @POST("eventos_reproductivos")
    Call<Void> upsertEventos(
            @Header("Prefer") String prefer,
            @Body JsonArray body
    );

    @GET("eventos_reproductivos")
    Call<List<EventoReproductivo>> getEventosDesdeFecha(
            @Query("select") String select,
            @Query("fecha_actualizacion") String fechaActualizacion,
            @Query("order") String order
    );
}