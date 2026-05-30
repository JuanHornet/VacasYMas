package com.example.vacasymas.data.models;

import com.google.gson.annotations.SerializedName;

public class CensoCercado {

    @SerializedName("id")
    private String id;

    @SerializedName("id_cercado")
    private String idCercado;

    @SerializedName("id_explotacion_uuid")
    private String idExplotacionUuid;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("vacas")
    private Integer vacas;

    @SerializedName("terneros")
    private Integer terneros;

    @SerializedName("toros")
    private Integer toros;

    @SerializedName("novillas")
    private Integer novillas;

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

    public CensoCercado() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdCercado() { return idCercado; }
    public void setIdCercado(String idCercado) { this.idCercado = idCercado; }

    public String getIdExplotacionUuid() { return idExplotacionUuid; }
    public void setIdExplotacionUuid(String idExplotacionUuid) { this.idExplotacionUuid = idExplotacionUuid; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public Integer getVacas() { return vacas; }
    public void setVacas(Integer vacas) { this.vacas = vacas; }

    public Integer getTerneros() { return terneros; }
    public void setTerneros(Integer terneros) { this.terneros = terneros; }

    public Integer getToros() { return toros; }
    public void setToros(Integer toros) { this.toros = toros; }

    public Integer getNovillas() { return novillas; }
    public void setNovillas(Integer novillas) { this.novillas = novillas; }

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

    public int getTotal() {
        return valor(vacas) + valor(terneros) + valor(toros) + valor(novillas);
    }

    private int valor(Integer n) {
        return n == null ? 0 : n;
    }
}
