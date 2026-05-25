package com.example.vacasymas.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnimalEnLista {

    @SerializedName("id")
    @Expose
    private String idDetalle;

    @SerializedName("id_lista")
    @Expose
    private String idLista;

    @SerializedName("id_animal")
    @Expose
    private String idAnimal;

    @SerializedName("crotal")
    @Expose
    private String crotal;

    @SerializedName("sexo")
    @Expose
    private String sexo;

    @SerializedName("marcado")
    @Expose
    private int marcado;

    @SerializedName("fecha_alta")
    @Expose
    private String fechaAlta;

    @SerializedName("observaciones")
    @Expose
    private String observaciones;

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

    public String getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(String idDetalle) {
        this.idDetalle = idDetalle;
    }

    public String getIdLista() {
        return idLista;
    }

    public void setIdLista(String idLista) {
        this.idLista = idLista;
    }

    public String getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(String idAnimal) {
        this.idAnimal = idAnimal;
    }

    public String getCrotal() {
        return crotal;
    }

    public void setCrotal(String crotal) {
        this.crotal = crotal;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public int getMarcado() {
        return marcado;
    }

    public void setMarcado(int marcado) {
        this.marcado = marcado;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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