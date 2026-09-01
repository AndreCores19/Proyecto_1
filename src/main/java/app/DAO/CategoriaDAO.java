package app.DAO;

import app.model.Categoria;
import app.model.Categorias;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.util.List;

public class CategoriaDAO {

    private static final String ARCHIVO = "categorias.xml";

    public List<Categoria> listar() {
        try {
            File archivo = new File(ARCHIVO);
            if (!archivo.exists()) {
                return new java.util.ArrayList<>();
            }
            JAXBContext context = JAXBContext.newInstance(Categorias.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Categorias categorias = (Categorias) unmarshaller.unmarshal(archivo);
            return categorias.getLista();
        } catch (JAXBException e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public void guardarLista(List<Categoria> lista) {
        try {
            Categorias categorias = new Categorias();
            categorias.setLista(lista);
            JAXBContext context = JAXBContext.newInstance(Categorias.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(categorias, new File(ARCHIVO));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


    public void agregar(Categoria nueva) {
        List<Categoria> lista = listar();
        int siguiente = lista.size() + 1;
        String nuevoId = String.format("CAT-%06d", siguiente);
        nueva.setId(nuevoId);
        lista.add(nueva);
        guardarLista(lista);
    }

    public Categoria buscarPorId(String id) {
        List<Categoria> lista = listar();
        for (Categoria f : lista) {
            if (f.getId().equals(id)) {
                return f;
            }
        }
        return null;
    }

    public void borrar(String id) {
        List<Categoria> lista = listar();
        for (Categoria f : lista) {
            if (f.getId().equals(id)) {
                lista.remove(f);

                break;
            }
        }
        guardarLista(lista);
    }
}