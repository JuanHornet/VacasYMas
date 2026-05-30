package com.example.vacasymas.data.models;

public class FiltroAnimales {

    private Integer estatus;
    private String sexo;
    private String estadoReproductivo;
    private String cercado;

    private Boolean sinCrotalIzquierdo;
    private Boolean sinCrotalDerecho;

    private Integer edadMinimaMeses;
    private Integer edadMaximaMeses;

    private Integer edadMinimaAnios;
    private Integer edadMaximaAnios;

    public Integer getEdadMinimaAnios() {
        return edadMinimaAnios;
    }

    public void setEdadMinimaAnios(Integer edadMinimaAnios) {
        this.edadMinimaAnios = edadMinimaAnios;
    }

    public Integer getEdadMaximaAnios() {
        return edadMaximaAnios;
    }

    public void setEdadMaximaAnios(Integer edadMaximaAnios) {
        this.edadMaximaAnios = edadMaximaAnios;
    }

    public Integer getEstatus() {
        return estatus;
    }

    public void setEstatus(Integer estatus) {
        this.estatus = estatus;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEstadoReproductivo() {
        return estadoReproductivo;
    }

    public void setEstadoReproductivo(String estadoReproductivo) {
        this.estadoReproductivo = estadoReproductivo;
    }

    public String getCercado() {
        return cercado;
    }

    public void setCercado(String cercado) {
        this.cercado = cercado;
    }

    public Boolean getSinCrotalIzquierdo() {
        return sinCrotalIzquierdo;
    }

    public void setSinCrotalIzquierdo(Boolean sinCrotalIzquierdo) {
        this.sinCrotalIzquierdo = sinCrotalIzquierdo;
    }

    public Boolean getSinCrotalDerecho() {
        return sinCrotalDerecho;
    }

    public void setSinCrotalDerecho(Boolean sinCrotalDerecho) {
        this.sinCrotalDerecho = sinCrotalDerecho;
    }

    public Integer getEdadMinimaMeses() {
        return edadMinimaMeses;
    }

    public void setEdadMinimaMeses(Integer edadMinimaMeses) {
        this.edadMinimaMeses = edadMinimaMeses;
    }

    public Integer getEdadMaximaMeses() {
        return edadMaximaMeses;
    }

    public void setEdadMaximaMeses(Integer edadMaximaMeses) {
        this.edadMaximaMeses = edadMaximaMeses;
    }


}
