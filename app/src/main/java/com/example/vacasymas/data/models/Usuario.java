package com.example.vacasymas.data.models;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("password")
    private String password;

    @SerializedName("fecha_actualizacion")
    private String fechaActualizacion;

    @SerializedName("sincronizado")
    private Integer sincronizado;

    @SerializedName("eliminado")
    private Integer eliminado;

    @SerializedName("fecha_eliminado")
    private String fechaEliminado;

    public Usuario() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(Integer sincronizado) {
        this.sincronizado = sincronizado;
    }

    public Integer getEliminado() {
        return eliminado;
    }

    public void setEliminado(Integer eliminado) {
        this.eliminado = eliminado;
    }

    public String getFechaEliminado() {
        return fechaEliminado;
    }

    public void setFechaEliminado(String fechaEliminado) {
        this.fechaEliminado = fechaEliminado;
    }
}