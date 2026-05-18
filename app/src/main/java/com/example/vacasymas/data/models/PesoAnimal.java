package com.example.vacasymas.data.models;

import com.google.gson.annotations.SerializedName;

public class PesoAnimal {

    @SerializedName("id")
    private String id;

    @SerializedName("id_animal")
    private String idAnimal;

    @SerializedName("id_explotacion_uuid")
    private String idExplotacionUuid;

    @SerializedName("crotal")
    private String crotal;

    @SerializedName("sexo")
    private String sexo;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("peso")
    private Integer peso;

    @SerializedName("observaciones")
    private String observaciones;

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

    public String getIdExplotacionUuid() { return idExplotacionUuid; }
    public void setIdExplotacionUuid(String idExplotacionUuid) { this.idExplotacionUuid = idExplotacionUuid; }

    public String getCrotal() { return crotal; }
    public void setCrotal(String crotal) { this.crotal = crotal; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public Integer getPeso() { return peso; }
    public void setPeso(Integer peso) { this.peso = peso; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Integer getSincronizado() { return sincronizado; }
    public void setSincronizado(Integer sincronizado) { this.sincronizado = sincronizado; }

    public Integer getEliminado() { return eliminado; }
    public void setEliminado(Integer eliminado) { this.eliminado = eliminado; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public String getFechaEliminado() { return fechaEliminado; }
    public void setFechaEliminado(String fechaEliminado) { this.fechaEliminado = fechaEliminado; }
}
