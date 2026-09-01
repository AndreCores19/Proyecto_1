package app.model;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlElement;
@XmlRootElement(name="categoria")
@XmlAccessorType(XmlAccessType.FIELD)

public class Categoria {

    @XmlElement
    private String id;
    @XmlElement
    private String descripcion;

    public Categoria(String id, String descripcion) {
        this.id=id;
        this.descripcion=descripcion;
    }

    public Categoria() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion=descripcion;
    }
}
