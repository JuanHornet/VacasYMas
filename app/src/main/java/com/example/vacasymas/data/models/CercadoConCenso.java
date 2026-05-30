package com.example.vacasymas.data.models;

public class CercadoConCenso {

    private Cercado cercado;
    private CensoCercado ultimoCenso;

    public CercadoConCenso(Cercado cercado, CensoCercado ultimoCenso) {
        this.cercado = cercado;
        this.ultimoCenso = ultimoCenso;
    }

    public Cercado getCercado() {
        return cercado;
    }

    public CensoCercado getUltimoCenso() {
        return ultimoCenso;
    }

    public int getTotalAnimales() {
        if (ultimoCenso == null) return 0;
        return ultimoCenso.getTotal();
    }
}