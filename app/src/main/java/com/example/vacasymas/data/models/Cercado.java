package com.example.vacasymas.data.models;

import com.google.gson.annotations.SerializedName;

public class Cercado {

    @SerializedName("id")
    private String id;

    @SerializedName("id_explotacion_uuid")
    private String idExplotacionUuid;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("superficie_ha")
    private Double superficieHa;

    @SerializedName("tipo")
    private String tipo;

    @SerializedName("observaciones")
    private String observaciones;

    @SerializedName("activo")
    private Integer activo;

    @SerializedName("sincronizado")
    private Integer sincronizado;

    @SerializedName("eliminado")
    private Integer eliminado;

    @SerializedName("fecha_actualizacion")
    private String fechaActualizacion;

    @SerializedName("fecha_eliminado")
    private String fechaEliminado;

    public Cercado() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdExplotacionUuid() { return idExplotacionUuid; }
    public void setIdExplotacionUuid(String idExplotacionUuid) { this.idExplotacionUuid = idExplotacionUuid; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getSuperficieHa() { return superficieHa; }
    public void setSuperficieHa(Double superficieHa) { this.superficieHa = superficieHa; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Integer getActivo() { return activo; }
    public void setActivo(Integer activo) { this.activo = activo; }

    public Integer getSincronizado() { return sincronizado; }
    public void setSincronizado(Integer sincronizado) { this.sincronizado = sincronizado; }

    public Integer getEliminado() { return eliminado; }
    public void setEliminado(Integer eliminado) { this.eliminado = eliminado; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public String getFechaEliminado() { return fechaEliminado; }
    public void setFechaEliminado(String fechaEliminado) { this.fechaEliminado = fechaEliminado; }
}