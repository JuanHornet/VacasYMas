package com.example.vacasymas.ui.diagnostico;

import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.data.models.DiagnosticoGestacion;

public class VacaDiagnosticoItem {

    private Animal animal;
    private DiagnosticoGestacion ultimoDiagnostico;

    public VacaDiagnosticoItem(Animal animal, DiagnosticoGestacion ultimoDiagnostico) {
        this.animal = animal;
        this.ultimoDiagnostico = ultimoDiagnostico;
    }

    public Animal getAnimal() {
        return animal;
    }

    public DiagnosticoGestacion getUltimoDiagnostico() {
        return ultimoDiagnostico;
    }
}
