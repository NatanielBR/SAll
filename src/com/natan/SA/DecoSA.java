package com.natan.SA;

import com.natan.abs.Anime;
import com.natan.abs.Decodificado;
import com.natan.abs.Decodificador;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DecoSA implements Decodificador {
    private final String HOST = "https://www.sakuraanimes.com";

    @Override
    public Decodificado decodificar() {
        Document doc = null;
        try {
            doc = Jsoup.connect(HOST + "/multimidia/episodioslegendados.html").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements ele = doc.select("td[headers=categorylist_header_title] a");
        List<Anime> animelist = new ArrayList<>();
        ele.forEach(a -> {
            String url = (HOST + a.attr("href"));
            String nome = (a.text()).replaceAll("[^0-9A-Za-z +]*", "");
            animelist.add(criarAnime(nome, url));
        });
        return new Decodificado() {
            @Override
            public List<Anime> getAnimeList() {
                return animelist;
            }

            @Override
            public Decodificador getDecodificador() {
                return DecoSA.this;
            }
        };
    }

    @Override
    public String getNomeDoDecodificador() {
        return "sakura animes";
    }

    @Override
    public String toString() {
        return "DecoSA";
    }
}
