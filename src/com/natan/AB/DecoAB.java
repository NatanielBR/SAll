package com.natan.AB;

import com.natan.abs.Anime;
import com.natan.abs.Decodificado;
import com.natan.abs.Decodificador;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DecoAB implements Decodificador {
    private final String HOST = "https://www.anbient.com";

    @Override
    public Decodificado decodificar() {
        try {
            Document document = Jsoup.connect(HOST + "/anime/lista").get();
            Elements elements = document.select("tr td[class=titulo] a");
            List<Anime> animelist = new ArrayList<>();
            for (Element e : elements) {
                animelist.add(criarAnime(e.text(), HOST + e.attr("href")));
            }
            return new Decodificado() {
                @Override
                public List<Anime> getAnimeList() {
                    return animelist;
                }

                @Override
                public Decodificador getDecodificador() {
                    return DecoAB.this;
                }
            };
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return new Decodificado() {
            @Override
            public List<Anime> getAnimeList() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public Decodificador getDecodificador() {
                return DecoAB.this;
            }
        };
    }

    @Override
    public String getNomeDoDecodificador() {
        return "anbient";
    }

    @Override
    public String toString() {
        return "DecoAB";
    }
}
