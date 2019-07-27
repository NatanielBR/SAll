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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class Principal implements Initializable {
    @FXML
    private ListView<Anime> lista;
    @FXML
    private Label status;
    @FXML
    private TextField busca;
    @FXML
    private Pane desativar;
    private Decodificador[] decodificadores;
    private List<Anime> decoRaiz;
    private HashMap<String, ImageView> decosIm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        decodificadores = new Decodificador[]{new DecoAB(), new DecoSA()};
        ServiceDecodificar serviceDecodificar = new ServiceDecodificar(decodificadores);
        decosIm = new HashMap<>();
        TaskUpdate taskUpdate = new TaskUpdate();
        lista.setCellFactory(param -> {
            return new ListCell<Anime>() {
                @Override
                protected void updateItem(Anime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) return;
                    setText(item.getNome());
                    setGraphic(new ImageView(item.getNomeDoDecodificador() + ".png"));
                }
            };
        });
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
            lista.getSelectionModel().clearSelection();
        });
        status.setOnMouseClicked((event -> {
            if (status.getUserData() != null) {
                Exception err = (Exception) status.getUserData();
                Alert alert = criarAlertaDeException(err);
                alert.setTitle(resources.getString("falhaLista"));
                alert.setHeaderText(err.getMessage());
                alert.showAndWait();
            }
        }));

        serviceDecodificar.stateProperty().addListener((a, b, c) -> {
            busca.setText("");
            if (c.equals(Worker.State.RUNNING)) {
                status.setText(resources.getString("obterLista"));
                status.setUserData(null);
                desativar.setDisable(true);
            } else if (c.equals(Worker.State.FAILED)) {
                status.setText(resources.getString("falhaLista"));
                status.setUserData(serviceDecodificar.getException());
                desativar.setDisable(false);
            } else if (c.equals(Worker.State.SUCCEEDED)) {
                List<Anime> animes = new ArrayList<>();
                for (Decodificado decodificado : serviceDecodificar.getValue()) {
                    animes.addAll(decodificado.getAnimeList());
                }
                lista.setItems(FXCollections.observableList(decoRaiz));
                int quantidade = lista.getItems().size();
                status.setText(MessageFormat.format(resources.getString("okLista"), quantidade,
                        quantidade > 1 ? resources.getString("animePlural") : resources.getString("animeSingular")));
                status.setUserData(null);
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

    private Alert criarAlertaDeException(Exception ex) {
        //Codigo não criado por mim, porem foi modificado
        //Codigo original: https://code.makery.ch/blog/javafx-dialogs-official/
        Alert alert = new Alert(Alert.AlertType.ERROR);

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
        return alert;
    }
}
