
package app.Logica;
import app.DTO.ExtraccionIADTO;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LogicaGemini {
    private static final String MODELO = "gemini-3.5-flash-lite"; //Se cambio de 2.5 a 3.5 flash lite porque la licencia de la KEY del correo institucional no funciona,
    // por lo que utilice la personal y ese es una de las versiones que funcionan por ser un "Nuevo usuario"
    private static final String ENDPOINT_BASE =
            "https://generativelanguage.googleapis.com/v1beta/models/";
    private final String apiKey;
    private final HttpClient httpClient;


    public LogicaGemini() {
        this.apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "No se encontro la variable de entorno GEMINI_API_KEY.");
        }
        this.httpClient = HttpClient.newHttpClient();
    }


    public String enviarMensaje(String textoUsuario) throws IOException, InterruptedException {
        String prompt = construirPrompt(textoUsuario);
        String url = ENDPOINT_BASE + MODELO + ":generateContent";
        JSONObject parte = new JSONObject().put("text", prompt);
        JSONObject contenido = new JSONObject()
                .put("parts", new JSONArray().put(parte));
        JSONObject cuerpo = new JSONObject()
                .put("contents", new JSONArray().put(contenido));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo.toString()))
                .build();
        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Error de la API (HTTP "
                    + response.statusCode() + "): " + response.body());
        }
        return extraerTexto(response.body());
    }


    private String construirPrompt(String textoUsuario) {
        return "Hoy es " + java.time.LocalDate.now() + ". Analiza la siguiente frase de un usuario que quiere hacer una reserva"
                + "Y responde EXCLUSIVAMENTE con un JSON válido, sin texto adicional, sin markdown, con esta estructura exacta:" +
                "{\n" +
                "              \"actividad\": \"string breve describiendo la actividad\",\n" +
                "              \"fecha\": \"YYYY-MM-DD\",\n" +
                "              \"horaInicio\": \"HH:mm\",\n" +
                "              \"horaFin\": \"HH:mm\",\n" +
                "              \"categorias\": [\"lista\", \"de\", \"categorias\", \"mencionadas\"]\n" +
                "            }" +
                "Si algun dato no se menciona en la frase, usa null en ese mismo campo. No agregues campos adicionales. La frase del usuario es: \"" + textoUsuario + "\""
                .formatted(LocalDate.now(), textoUsuario);
    }


    private String extraerTexto(String jsonRespuesta) {
        JSONObject raiz = new JSONObject(jsonRespuesta);
        JSONArray candidatos = raiz.getJSONArray("candidates");
        JSONObject primerCandidato = candidatos.getJSONObject(0);
        JSONObject contenido = primerCandidato.getJSONObject("content");
        JSONArray partes = contenido.getJSONArray("parts");
        return partes.getJSONObject(0).getString("text");
    }


    public ExtraccionIADTO extraerDatosDesdeLaFrase(String frase) throws IOException, InterruptedException {
        String respuesta = enviarMensaje(frase);
        String jsonLimpio = limpiar(respuesta);
        return mapearExtraccionIADTO(jsonLimpio);
    }


    private ExtraccionIADTO mapearExtraccionIADTO(String jsonLimpio) {
        JSONObject json = new JSONObject(jsonLimpio);
        ExtraccionIADTO dto = new ExtraccionIADTO();

        dto.setActividad(json.optString("actividad", null));
        if (json.has("fecha") && !json.isNull("fecha")) {
            dto.setFecha(LocalDate.parse(json.getString("fecha")));
        }
        if (json.has("horaInicio") && !json.isNull("horaInicio")) {
            dto.setHoraInicio(LocalTime.parse(json.getString("horaInicio")));
        }
        if (json.has("horaFin") && !json.isNull("horaFin")) {
            dto.setHoraFin(LocalTime.parse(json.getString("horaFin")));
        }
        List<String> categorias = new ArrayList<>();
        if (json.has("categorias") && !json.isNull("categorias")) {
            JSONArray arr = json.getJSONArray("categorias");
            for (int i = 0; i < arr.length(); i++) {
                categorias.add(arr.getString(i));
            }
        }
        dto.setCategoriasSugeridas(categorias);
        return dto;
    }


    private String limpiar(String texto) {
        return texto.replaceAll("```json", "").replaceAll("```", "").trim();
    }

}