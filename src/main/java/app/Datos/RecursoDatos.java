package app.Datos;

import app.DTO.RecursoDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecursoDatos {

    private String rutaArchivo;
    private List<RecursoDTO> listado;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public RecursoDatos(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.listado = new ArrayList<>();
    }

    public RecursoDatos() {
        this.listado = new ArrayList<>();
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public List<RecursoDTO> getListado() {
        return listado;
    }

    public void setListado(List<RecursoDTO> listado) {
        this.listado = listado;
    }

    public void serializar() {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            gson.toJson(listado, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deserializar() {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            listado = new ArrayList<>();
            return;
        }
        try (FileReader reader = new FileReader(archivo)) {
            Type tipoLista = new TypeToken<List<RecursoDTO>>(){}.getType();
            List<RecursoDTO> lista = gson.fromJson(reader, tipoLista);
            listado = (lista != null) ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            listado = new ArrayList<>();
        }
    }
}