package app.Datos;

import app.DTO.FuncionarioDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDatos {

    private String rutaArchivo;
    private List<FuncionarioDTO> listado;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public FuncionarioDatos(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.listado = new ArrayList<>();
    }

    public FuncionarioDatos() {
        this.listado = new ArrayList<>();
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public List<FuncionarioDTO> getListado() {
        return listado;
    }

    public void setListado(List<FuncionarioDTO> listado) {
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
            Type tipoLista = new TypeToken<List<FuncionarioDTO>>(){}.getType();
            List<FuncionarioDTO> lista = gson.fromJson(reader, tipoLista);
            listado = (lista != null) ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            listado = new ArrayList<>();
        }
    }
}