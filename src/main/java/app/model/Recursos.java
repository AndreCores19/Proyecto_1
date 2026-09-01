package app.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "recursos")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recursos {

    @XmlElement(name = "recurso")
    private List<Recurso> lista = new ArrayList<>();

    public List<Recurso> getLista() {
        return lista;
    }

    public void setLista(List<Recurso> lista) {
        this.lista = lista;
    }
}