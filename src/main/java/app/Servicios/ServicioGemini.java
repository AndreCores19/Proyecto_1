package app.Servicios;

import app.Logica.LogicaGemini;
import app.DTO.ExtraccionIADTO;

import java.io.IOException;

public class ServicioGemini {
    private static final LogicaGemini logicaGemini = new LogicaGemini();

    public static ExtraccionIADTO extraerDatosDeLaIA(String frase) throws IOException, InterruptedException {
        return logicaGemini.extraerDatosDesdeLaFrase(frase);
    }
}
