package com.example.vacasymas.data.models;

import com.google.gson.annotations.SerializedName;

public class Animal {

    @SerializedName("id")
    private String id;

    @SerializedName("id_sharepoint")
    private Long idSharepoint;

    @SerializedName("crotal")
    private String crotal;

    @SerializedName("id_explotacion_uuid")
    private String idExplotacionUuid;

    @SerializedName("estatus")
    private Integer estatus;

    @SerializedName("fecha_nacimiento")
    private String fechaNacimiento;

    @SerializedName("raza")
    private String raza;

    @SerializedName("sexo")
    private String sexo;

    @SerializedName("crotal_madre")
    private String crotalMadre;

    @SerializedName("capa")
    private String capa;

    @SerializedName("cercado")
    private String cercado;

    @SerializedName("id_cercado_historico")
    private Long idCercadoHistorico;

    @SerializedName("paridera")
    private String paridera;

    @SerializedName("alta_gestionada")
    private String altaGestionada;

    @SerializedName("statecode")
    private String statecode;

    @SerializedName("factor")
    private String factor;

    @SerializedName("fecha_baja_explotacion")
    private String fechaBajaExplotacion;

    @SerializedName("causa_alta")
    private String causaAlta;

    @SerializedName("explotacion_nacimiento")
    private String explotacionNacimiento;

    @SerializedName("check_paridera")
    private String checkParidera;

    @SerializedName("ischosen")
    private String ischosen;

    @SerializedName("estado_reproductivo")
    private String estadoReproductivo;

    @SerializedName("crotal_izquierdo_presente")
    private Boolean crotalIzquierdoPresente;

    @SerializedName("crotal_derecho_presente")
    private Boolean crotalDerechoPresente;

    @SerializedName("fecha_actualizacion")
    private String fechaActualizacion;

    @SerializedName("sincronizado")
    private Integer sincronizado;

    @SerializedName("eliminado")
    private Integer eliminado;

    @SerializedName("fecha_eliminado")
    private String fechaEliminado;

    private String nombreExplotacion;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getIdSharepoint() { return idSharepoint; }
    public void setIdSharepoint(Long idSharepoint) { this.idSharepoint = idSharepoint; }

    public String getCrotal() { return crotal; }
    public void setCrotal(String crotal) { this.crotal = crotal; }

    public String getIdExplotacionUuid() { return idExplotacionUuid; }
    public void setIdExplotacionUuid(String idExplotacionUuid) { this.idExplotacionUuid = idExplotacionUuid; }

    public Integer getEstatus() { return estatus; }
    public void setEstatus(Integer estatus) { this.estatus = estatus; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getCrotalMadre() { return crotalMadre; }
    public void setCrotalMadre(String crotalMadre) { this.crotalMadre = crotalMadre; }

    public String getCapa() { return capa; }
    public void setCapa(String capa) { this.capa = capa; }

    public String getCercado() { return cercado; }
    public void setCercado(String cercado) { this.cercado = cercado; }

    public Long getIdCercadoHistorico() { return idCercadoHistorico; }
    public void setIdCercadoHistorico(Long idCercadoHistorico) { this.idCercadoHistorico = idCercadoHistorico; }

    public String getParidera() { return paridera; }
    public void setParidera(String paridera) { this.paridera = paridera; }

    public String getAltaGestionada() { return altaGestionada; }
    public void setAltaGestionada(String altaGestionada) { this.altaGestionada = altaGestionada; }

    public String getStatecode() { return statecode; }
    public void setStatecode(String statecode) { this.statecode = statecode; }

    public String getFactor() { return factor; }
    public void setFactor(String factor) { this.factor = factor; }

    public String getFechaBajaExplotacion() { return fechaBajaExplotacion; }
    public void setFechaBajaExplotacion(String fechaBajaExplotacion) { this.fechaBajaExplotacion = fechaBajaExplotacion; }

    public String getCausaAlta() { return causaAlta; }
    public void setCausaAlta(String causaAlta) { this.causaAlta = causaAlta; }

    public String getExplotacionNacimiento() { return explotacionNacimiento; }
    public void setExplotacionNacimiento(String explotacionNacimiento) { this.explotacionNacimiento = explotacionNacimiento; }

    public String getCheckParidera() { return checkParidera; }
    public void setCheckParidera(String checkParidera) { this.checkParidera = checkParidera; }

    public String getIschosen() { return ischosen; }
    public void setIschosen(String ischosen) { this.ischosen = ischosen; }

    public String getEstadoReproductivo() { return estadoReproductivo;    }

    public void setEstadoReproductivo(String estadoReproductivo) { this.estadoReproductivo = estadoReproductivo;    }

    public Boolean getCrotalIzquierdoPresente() { return crotalIzquierdoPresente; }
    public void setCrotalIzquierdoPresente(Boolean crotalIzquierdoPresente) { this.crotalIzquierdoPresente = crotalIzquierdoPresente; }

    public Boolean getCrotalDerechoPresente() { return crotalDerechoPresente; }
    public void setCrotalDerechoPresente(Boolean crotalDerechoPresente) { this.crotalDerechoPresente = crotalDerechoPresente; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public Integer getSincronizado() { return sincronizado; }
    public void setSincronizado(Integer sincronizado) { this.sincronizado = sincronizado; }

    public Integer getEliminado() { return eliminado; }
    public void setEliminado(Integer eliminado) { this.eliminado = eliminado; }

    public String getFechaEliminado() { return fechaEliminado; }
    public void setFechaEliminado(String fechaEliminado) { this.fechaEliminado = fechaEliminado; }

    public String getNombreExplotacion() { return nombreExplotacion; }
    public void setNombreExplotacion(String nombreExplotacion) { this.nombreExplotacion = nombreExplotacion; }

    public String getEstatusDescripcion() {
        if (estatus == null) return "-";
        switch (estatus) {
            case 10001: return "ternero macho";
            case 10002: return "ternera hembra";
            case 10003: return "vaca";
            case 10004: return "toro";
            case 10005: return "novilla";
            case 10006: return "animal vendido";
            case 10007: return "animal fallecido";
            case 10008: return "animal desaparecido";
            case 10010: return "animal positivo en tuberculosis";
            default: return String.valueOf(estatus);
        }
    }
    public String getEstadoReproductivoMostrar() {
        if (estadoReproductivo == null || estadoReproductivo.trim().isEmpty()) {
            return "Nada";
        }

        switch (estadoReproductivo.toLowerCase()) {
            case "vacia":
                return "Vacía";
            case "cubierta":
                return "Cubierta";
            case "preñada":
                return "Preñada";
            case "nada":
            default:
                return "Nada";
        }
    }
}