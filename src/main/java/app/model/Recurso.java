package app.model;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlElement;
@XmlRootElement(name="recurso")
@XmlAccessorType(XmlAccessType.FIELD)

public class Recurso {
    @XmlElement
    private String numActivo; //numero de activo
    @XmlElement
    private Categoria categoria;
    @XmlElement
    private String descripcion;

    public Recurso(String numActivo, Categoria categoria, String descripcion) {
        this.numActivo=numActivo;
        this.categoria=categoria;
        this.descripcion=descripcion;
    }

    public Recurso() {

    }

    public String getId() {
        return numActivo;
    }

    public void setId(String numActivo) {
        this.numActivo=numActivo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria=categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion=descripcion;
    }
}
