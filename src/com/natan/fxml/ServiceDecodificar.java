package com.natan.fxml;

import com.natan.abs.Decodificado;
import com.natan.abs.Decodificador;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

public class ServiceDecodificar extends Service<List<Decodificado>> {
    private Decodificador[] decodificador;

    public ServiceDecodificar(Decodificador[] decodificador) {
        this.decodificador = decodificador;
    }

    @Override
    protected Task<List<Decodificado>> createTask() {
        return new Task<List<Decodificado>>() {
            @Override
            protected List<Decodificado> call() throws Exception {
                List<Decodificado> decodificados = new ArrayList<>();
                for (Decodificador decor : decodificador) {
                    decodificados.add(decor.decodificar());
                }
                return decodificados;
            }
        };
    }
}
