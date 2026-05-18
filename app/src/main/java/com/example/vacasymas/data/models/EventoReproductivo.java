package com.example.vacasymas.data.models;

import com.google.gson.annotations.SerializedName;

public class EventoReproductivo {

    @SerializedName("id")
    private String id;

    @SerializedName("id_madre")
    private String idMadre;

    @SerializedName("id_cria")
    private String idCria;

    @SerializedName("id_explotacion_uuid")
    private String idExplotacionUuid;

    @SerializedName("tipo_evento")
    private String tipoEvento;

    @SerializedName("crotalMadre")
    private String crotalMadre;

    @SerializedName("fecha_evento")
    private String fechaEvento;

    @SerializedName("resultado_cria")
    private String resultadoCria;

    @SerializedName("cria_identificada")
    private Integer criaIdentificada;

    @SerializedName("sexo_estimado")
    private String sexoEstimado;

    @SerializedName("raza_estimada")
    private String razaEstimada;

    @SerializedName("capa_estimada")
    private String capaEstimada;

    @SerializedName("cercado")
    private String cercado;

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

    public static final String TIPO_PARTO = "PARTO";

    public static final String VIVA_IDENTIFICADA = "VIVA_IDENTIFICADA";
    public static final String VIVA_PENDIENTE_IDENTIFICAR = "VIVA_PENDIENTE_IDENTIFICAR";
    public static final String NACIDA_MUERTA = "NACIDA_MUERTA";
    public static final String MUERE_ANTES_IDENTIFICAR = "MUERE_ANTES_IDENTIFICAR";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdMadre() {
        return idMadre;
    }

    public void setIdMadre(String idMadre) {
        this.idMadre = idMadre;
    }

    public String getIdCria() {
        return idCria;
    }

    public void setIdCria(String idCria) {
        this.idCria = idCria;
    }

    public String getIdExplotacionUuid() {
        return idExplotacionUuid;
    }

    public void setIdExplotacionUuid(String idExplotacionUuid) {
        this.idExplotacionUuid = idExplotacionUuid;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getCrotalMadre() {
        return crotalMadre;
    }

    public void setCrotalMadre(String crotalMadre) {
        this.crotalMadre = crotalMadre;
    }

    public String getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(String fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String getResultadoCria() {
        return resultadoCria;
    }

    public void setResultadoCria(String resultadoCria) {
        this.resultadoCria = resultadoCria;
    }

    public Integer getCriaIdentificada() {
        return criaIdentificada;
    }

    public void setCriaIdentificada(Integer criaIdentificada) {
        this.criaIdentificada = criaIdentificada;
    }

    public String getSexoEstimado() {
        return sexoEstimado;
    }

    public void setSexoEstimado(String sexoEstimado) {
        this.sexoEstimado = sexoEstimado;
    }

    public String getRazaEstimada() {
        return razaEstimada;
    }

    public void setRazaEstimada(String razaEstimada) {
        this.razaEstimada = razaEstimada;
    }

    public String getCapaEstimada() {
        return capaEstimada;
    }

    public void setCapaEstimada(String capaEstimada) {
        this.capaEstimada = capaEstimada;
    }

    public String getCercado() {
        return cercado;
    }

    public void setCercado(String cercado) {
        this.cercado = cercado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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
