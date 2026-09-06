package app.Servicios;

import app.DTO.FuncionarioDTO;
import app.Logica.FuncionarioLogica;

import java.util.List;

public class ServicioFuncionario {
    private FuncionarioLogica funcionarioLogica = new FuncionarioLogica();

    public List<FuncionarioDTO> listarTodos() {
        return funcionarioLogica.listarTodos();
    }

    public FuncionarioDTO buscarPorId(String id) throws Exception {
        return funcionarioLogica.buscarPorId(id);
    }

    public List<FuncionarioDTO> buscarPorNombre(String texto) {
        return funcionarioLogica.buscarPorNombre(texto);
    }

    public void agregar(FuncionarioDTO nuevo) throws Exception {
        funcionarioLogica.agregar(nuevo);
    }

    public void modificar(FuncionarioDTO actualizado) throws Exception {
        funcionarioLogica.modificar(actualizado);
    }

    public void eliminar(String id) throws Exception {
        funcionarioLogica.eliminar(id);
    }
}