package com.example.vacasymas.data.models;

import com.google.gson.annotations.SerializedName;

public class Explotacion {

    @SerializedName("id")
    private String id;

    @SerializedName("id_sharepoint")
    private Long idSharepoint;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("fecha_actualizacion")
    private String fechaActualizacion;

    @SerializedName("sincronizado")
    private Integer sincronizado;

    @SerializedName("eliminado")
    private Integer eliminado;

    @SerializedName("fecha_eliminado")
    private String fechaEliminado;

    @SerializedName("id_usuario")
    private String idUsuario;

    public Explotacion() {}

    public Explotacion(String id, String nombre, String idUsuario) {
        this.id = id;
        this.nombre = nombre;
        this.idUsuario = idUsuario;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getIdSharepoint() { return idSharepoint; }
    public void setIdSharepoint(Long idSharepoint) { this.idSharepoint = idSharepoint; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public Integer getSincronizado() { return sincronizado; }
    public void setSincronizado(Integer sincronizado) { this.sincronizado = sincronizado; }

    public Integer getEliminado() { return eliminado; }
    public void setEliminado(Integer eliminado) { this.eliminado = eliminado; }

    public String getFechaEliminado() { return fechaEliminado; }
    public void setFechaEliminado(String fechaEliminado) { this.fechaEliminado = fechaEliminado; }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
