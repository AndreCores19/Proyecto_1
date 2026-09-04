package app.Datos;

import app.DTO.ReservaDTO;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservaDatos {
    private String rutaArchivo;
    private List<ReservaDTO> reservas;
    private Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).registerTypeAdapter(LocalTime.class, new LocalTimeAdapter()).create();

    public ReservaDatos(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.reservas = new ArrayList<>();
    }

    public ReservaDatos() {
        this.reservas = new ArrayList<>();
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public List<ReservaDTO> getReservas() {
        return reservas;
    }

    public void setReservas(List<ReservaDTO> reservas) {
        this.reservas = reservas;
    }

    public void serializar() {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            gson.toJson(reservas, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deserializar() {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            reservas = new ArrayList<>();
            return;
        }
        try (FileReader reader = new FileReader(archivo)) {
            Type tipo = new TypeToken<List<ReservaDTO>>() {
            }.getType();
            List<ReservaDTO> reser = gson.fromJson(reader, tipo);
            reservas = (reser != null) ? reser : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            reservas = new ArrayList<>();
        }
    }

    private class LocalTimeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

        private static final DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");

        @Override
        public LocalTime deserialize(JsonElement json, Type tipo, JsonDeserializationContext contexto) throws JsonParseException {
            return LocalTime.parse(json.getAsString(), formato);
        }

        @Override
        public JsonElement serialize(LocalTime hora, Type tipo, JsonSerializationContext contexto) {
            return new JsonPrimitive(hora.format(formato));
        }
    }

    private class LocalDateAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalDate> {

        private static final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd:MM:yyyy");

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsString(), formato);
        }

        @Override
        public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(formato));
        }
    }
}