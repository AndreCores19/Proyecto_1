package app.DTO;
import java.util.LinkedHashMap;
import java.util.Map;

public class MatrizDTO {
    private String hora;

    private Map<String, String> estadoPorRecurso = new LinkedHashMap<>();

    public MatrizDTO() {}

    public MatrizDTO(String hora) {
        this.hora = hora;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Map<String, String> getEstadoPorRecurso() {
        return estadoPorRecurso;
    }

    public void setEstadoPorRecurso(Map<String, String> estadoPorRecurso) {
        this.estadoPorRecurso = estadoPorRecurso;
    }
}