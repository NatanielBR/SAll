package com.natan.fxml;

import javafx.concurrent.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TaskUpdate extends Task<String> {
    @Override
    protected String call() throws Exception {
        Document doc = Jsoup.connect("https://github.com/NatanielBR/SAll/releases/latest").get();
        return doc.select("span[class=css-truncate-target]").first().text();
    }
}
