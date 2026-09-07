package app.DTO;

import java.util.ArrayList;
import java.util.List;

public class ResultadoDeAsignacionDTO {
    private boolean exitosa;
    private List<String> idsCategoriasNoDisponibles = new ArrayList<>();
    private List<String> idsRecursosAsignados = new ArrayList<>();

    public ResultadoDeAsignacionDTO(boolean exitosa, List<String> idsCategoriasNoDisponibles, List<String> idsRecursosAsignados) {
        this.exitosa = exitosa;
        this.idsCategoriasNoDisponibles = idsCategoriasNoDisponibles;
        this.idsRecursosAsignados = idsRecursosAsignados;
    }

    public boolean isExitosa() {
        return exitosa;
    }

    public List<String> getIdsCategoriasNoDisponibles() {
        return idsCategoriasNoDisponibles;
    }

    public List<String> getIdsRecursosAsignados() {
        return idsRecursosAsignados;
    }

    public void setExitosa(boolean exitosa) {
        this.exitosa = exitosa;
    }

    public void setIdsCategoriasNoDisponibles(List<String> idsCategoriasNoDisponibles) {
        this.idsCategoriasNoDisponibles = idsCategoriasNoDisponibles;
    }

    public void setIdsRecursosAsignados(List<String> idsRecursosAsignados) {
        this.idsRecursosAsignados = idsRecursosAsignados;
    }
}
