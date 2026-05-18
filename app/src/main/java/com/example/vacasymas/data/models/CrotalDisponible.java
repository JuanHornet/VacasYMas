package com.example.vacasymas.data.models;

import com.google.gson.annotations.SerializedName;

public class CrotalDisponible {

    public static final String DISPONIBLE = "DISPONIBLE";
    public static final String USADO = "USADO";
    public static final String ANULADO = "ANULADO";

    @SerializedName("id")
    private String id;

    @SerializedName("crotal")
    private String crotal;

    @SerializedName("id_explotacion_uuid")
    private String idExplotacionUuid;

    @SerializedName("estado")
    private String estado;

    @SerializedName("fecha_asignacion")
    private String fechaAsignacion;

    @SerializedName("fecha_uso")
    private String fechaUso;

    @SerializedName("id_animal_usado")
    private String idAnimalUsado;

    @SerializedName("observaciones")
    private String observaciones;

    @SerializedName("fecha_actualizacion")
    private String fechaActualizacion;

    @SerializedName("sincronizado")
    private Integer sincronizado;

    @SerializedName("eliminado")
    private Integer eliminado;

    @SerializedName("fecha_eliminado")
    private String fechaEliminado;

    public CrotalDisponible() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCrotal() { return crotal; }
    public void setCrotal(String crotal) { this.crotal = crotal; }

    public String getIdExplotacionUuid() { return idExplotacionUuid; }
    public void setIdExplotacionUuid(String idExplotacionUuid) { this.idExplotacionUuid = idExplotacionUuid; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(String fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public String getFechaUso() { return fechaUso; }
    public void setFechaUso(String fechaUso) { this.fechaUso = fechaUso; }

    public String getIdAnimalUsado() { return idAnimalUsado; }
    public void setIdAnimalUsado(String idAnimalUsado) { this.idAnimalUsado = idAnimalUsado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public Integer getSincronizado() { return sincronizado; }
    public void setSincronizado(Integer sincronizado) { this.sincronizado = sincronizado; }

    public Integer getEliminado() { return eliminado; }
    public void setEliminado(Integer eliminado) { this.eliminado = eliminado; }

    public String getFechaEliminado() { return fechaEliminado; }
    public void setFechaEliminado(String fechaEliminado) { this.fechaEliminado = fechaEliminado; }
}
