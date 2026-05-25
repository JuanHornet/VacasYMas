package com.example.vacasymas.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListaAnimal {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("id_explotacion_uuid")
    @Expose
    private String idExplotacionUuid;

    @SerializedName("nombre")
    @Expose
    private String nombre;

    @SerializedName("tipo")
    @Expose
    private String tipo;

    @SerializedName("observaciones")
    @Expose
    private String observaciones;

    @SerializedName("fecha_creacion")
    @Expose
    private String fechaCreacion;

    @SerializedName("sincronizado")
    @Expose
    private int sincronizado;

    @SerializedName("eliminado")
    @Expose
    private int eliminado;

    @SerializedName("fecha_actualizacion")
    @Expose
    private String fechaActualizacion;

    @SerializedName("fecha_eliminado")
    @Expose
    private String fechaEliminado;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdExplotacionUuid() {
        return idExplotacionUuid;
    }

    public void setIdExplotacionUuid(String idExplotacionUuid) {
        this.idExplotacionUuid = idExplotacionUuid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }

    public int getEliminado() {
        return eliminado;
    }

    public void setEliminado(int eliminado) {
        this.eliminado = eliminado;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getFechaEliminado() {
        return fechaEliminado;
    }

    public void setFechaEliminado(String fechaEliminado) {
        this.fechaEliminado = fechaEliminado;
    }
}