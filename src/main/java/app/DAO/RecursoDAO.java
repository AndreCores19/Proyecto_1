package app.DAO;

import app.model.Recurso;
import app.model.Recursos;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.util.List;

public class RecursoDAO {

    private static final String ARCHIVO = "recursos.xml";

    public List<Recurso> listar() {
        try {
            File archivo = new File(ARCHIVO);
            if (!archivo.exists()) {
                return new java.util.ArrayList<>();
            }
            JAXBContext context = JAXBContext.newInstance(Recursos.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Recursos recursos = (Recursos) unmarshaller.unmarshal(archivo);
            return recursos.getLista();
        } catch (JAXBException e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public void guardarLista(List<Recurso> lista) {
        try {
            Recursos recursos = new Recursos();
            recursos.setLista(lista);
            JAXBContext context = JAXBContext.newInstance(Recursos.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(recursos, new File(ARCHIVO));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public void agregar(Recurso nuevo) {
        List<Recurso> lista = listar();
        if (buscarPorNumActivo(nuevo.getId()) == null) {
            lista.add(nuevo);
            guardarLista(lista);
        }
    }

    public Recurso buscarPorNumActivo(String numActivo) {
        List<Recurso> lista = listar();
        for (Recurso r : lista) {
            if (r.getId().equals(numActivo)) {
                return r;
            }
        }
        return null;
    }

    public void borrar(String numActivo) {
        List<Recurso> lista = listar();
        for (Recurso r : lista) {
            if (r.getId().equals(numActivo)) {
                lista.remove(r);
                break;
            }
        }
        guardarLista(lista);
    }
}