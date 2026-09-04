package app.Datos;

import app.DTO.CategoriaDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDatos {

    private String rutaArchivo;
    private List<CategoriaDTO> listado;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public CategoriaDatos(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.listado = new ArrayList<>();
    }

    public CategoriaDatos() {
        this.listado = new ArrayList<>();
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public List<CategoriaDTO> getListado() {
        return listado;
    }

    public void setListado(List<CategoriaDTO> listado) {
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
            Type tipoLista = new TypeToken<List<CategoriaDTO>>(){}.getType();
            List<CategoriaDTO> lista = gson.fromJson(reader, tipoLista);
            listado = (lista != null) ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            listado = new ArrayList<>();
        }
    }
}