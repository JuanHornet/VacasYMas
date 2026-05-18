package com.example.vacasymas.data.models;

import com.google.gson.annotations.SerializedName;

public class NotaAnimal {

    @SerializedName("id")
    private String id;

    @SerializedName("id_animal")
    private String idAnimal;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("texto")
    private String texto;

    @SerializedName("sincronizado")
    private Integer sincronizado;

    @SerializedName("eliminado")
    private Integer eliminado;

    @SerializedName("fecha_actualizacion")
    private String fechaActualizacion;

    @SerializedName("fecha_eliminado")
    private String fechaEliminado;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdAnimal() { return idAnimal; }
    public void setIdAnimal(String idAnimal) { this.idAnimal = idAnimal; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public Integer getSincronizado() { return sincronizado; }
    public void setSincronizado(Integer sincronizado) { this.sincronizado = sincronizado; }

    public Integer getEliminado() { return eliminado; }
    public void setEliminado(Integer eliminado) { this.eliminado = eliminado; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public String getFechaEliminado() { return fechaEliminado; }
    public void setFechaEliminado(String fechaEliminado) { this.fechaEliminado = fechaEliminado; }
}
