package app.DAO;

import app.model.Funcionario;
import app.model.Funcionarios;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.util.List;

public class FuncionarioDAO {

    private static final String ARCHIVO = "funcionarios.xml";

    public List<Funcionario> listar() {
        try {
            File archivo = new File(ARCHIVO);
            if (!archivo.exists()) {
                return new java.util.ArrayList<>();
            }
            JAXBContext context = JAXBContext.newInstance(Funcionarios.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Funcionarios funcionarios = (Funcionarios) unmarshaller.unmarshal(archivo);
            return funcionarios.getLista();
        } catch (JAXBException e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public void guardarLista(List<Funcionario> lista) {
        try {
            Funcionarios funcionarios = new Funcionarios();
            funcionarios.setLista(lista);
            JAXBContext context = JAXBContext.newInstance(Funcionarios.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(funcionarios, new File(ARCHIVO));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public void agregar(Funcionario nuevo) {
        List<Funcionario> lista = listar();
        if (buscarPorId(nuevo.getId()) == null) {
            lista.add(nuevo);
            guardarLista(lista);
        }
    }

    public Funcionario buscarPorId(String id) {
        List<Funcionario> lista = listar();
            for (Funcionario f : lista) {
                if (f.getId().equals(id)) {
                    return f;
                }
            }
        return null;
    }

    public void borrar(String id) {
        List<Funcionario> lista = listar();
        for (Funcionario f : lista) {
            if (f.getId().equals(id)) {
                lista.remove(f);

                break;
            }
        }
        guardarLista(lista);
    }
}