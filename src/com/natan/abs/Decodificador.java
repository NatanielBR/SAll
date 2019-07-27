package com.natan.abs;

public interface Decodificador {
    Decodificado decodificar();

    String getNomeDoDecodificador();
    default Anime criarAnime(String nome, String URL) {
        return new Anime() {
            @Override
            public String getNome() {
                return nome;
            }

            @Override
            public String getNomeDoDecodificador() {
                return Decodificador.this.getNomeDoDecodificador();
            }

            @Override
            public String getUrlPost() {
                return URL;
            }

            @Override
            public String toString() {
                return getNome();
            }
        };
    }
}
