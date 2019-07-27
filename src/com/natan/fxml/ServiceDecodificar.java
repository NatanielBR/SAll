package com.natan.fxml;

import com.natan.abs.Decodificado;
import com.natan.abs.Decodificador;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.concurrent.atomic.AtomicReference;

public class ServiceDecodificar extends Service<Decodificado> {
    private AtomicReference<Decodificador> decodificador;

    public ServiceDecodificar(AtomicReference<Decodificador> decodificador) {
        this.decodificador = decodificador;
    }

    @Override
    protected Task<Decodificado> createTask() {
        return new Task<Decodificado>() {
            @Override
            protected Decodificado call() throws Exception {
                return decodificador.get().decodificar();
            }
        };
    }
}
