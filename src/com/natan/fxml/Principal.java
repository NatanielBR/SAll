package com.natan.fxml;

import com.natan.AB.DecoAB;
import com.natan.SA.DecoSA;
import com.natan.abs.Anime;
import com.natan.abs.Decodificado;
import com.natan.abs.Decodificador;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class Principal implements Initializable {
    @FXML
    private ListView<Anime> lista;
    @FXML
    private ChoiceBox<Decodificador> decos;
    @FXML
    private Label status;
    @FXML
    private TextField busca;
    @FXML
    private Pane desativar;
    private AtomicReference<Decodificador> decodificador;
    private List<Anime> decoRaiz;

    public Principal() {
        decodificador = new AtomicReference<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ServiceDecodificar serviceDecodificar = new ServiceDecodificar(decodificador);
        TaskUpdate taskUpdate = new TaskUpdate();

        decos.setItems(FXCollections.observableArrayList(new DecoAB(), new DecoSA()));
        decos.getSelectionModel().select(0);
        decos.getSelectionModel().selectedItemProperty().addListener((a, b, c) -> {
            decodificador.set(c);
            serviceDecodificar.restart();
        });
        decodificador.set(decos.getValue()); //selecionar Anbient
        busca.textProperty().addListener((a, b, c) -> {
            if (!c.isEmpty()) {
                final String cc = c.toLowerCase();
                List<Anime> filtro = decoRaiz.stream().filter((aa) -> aa.getNome().toLowerCase().contains(cc)).collect(Collectors.toList());
                lista.setItems(FXCollections.observableList(filtro));
            } else {
                lista.setItems(FXCollections.observableList(decoRaiz));
            }
        });
        lista.setOnMouseClicked(event -> {
            try {
                Desktop.getDesktop().browse(new URL(lista.getSelectionModel().getSelectedItem().getUrlPost()).toURI());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serviceDecodificar.stateProperty().addListener((a, b, c) -> {
            busca.setText("");
            if (c.equals(Worker.State.RUNNING)) {
                status.setText(resources.getString("obterLista"));
                desativar.setDisable(true);
            } else if (c.equals(Worker.State.FAILED)) {
                status.setText(resources.getString("falhaLista"));
                desativar.setDisable(false);
            } else if (c.equals(Worker.State.SUCCEEDED)) {
                Decodificado value = serviceDecodificar.getValue();
                decoRaiz = value.getAnimeList();
                lista.setItems(FXCollections.observableList(decoRaiz));
                int quantidade = lista.getItems().size();
                status.setText(MessageFormat.format(resources.getString("okLista"), quantidade,
                        quantidade > 1 ? resources.getString("animePlural") : resources.getString("animeSingular")));
                desativar.setDisable(false);
            }
        });
        taskUpdate.stateProperty().addListener((a, b, c) -> {
            if (c.equals(Worker.State.SUCCEEDED)) {
                String verSion = taskUpdate.getValue();
                double version = Double.valueOf(verSion);
                if (version > Double.valueOf(resources.getString("versao"))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(resources.getString("novaVersaoTitulo"));
                    alert.setContentText(MessageFormat.format(resources.getString("novaVersaoMessagem"), verSion));
                    alert.showAndWait();
                    System.exit(0);
                } else {
                    serviceDecodificar.start();
                }
            }
        });
        new Thread(taskUpdate).start();

    }
}
