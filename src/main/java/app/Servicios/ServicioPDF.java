package app.Servicios;

public class ServicioPDF {
    public void abrirReporte(String ruta) throws Exception {
        try {
            java.io.File archivo = new java.io.File(ruta);
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(archivo);
            }
        } catch (Exception e) {
            throw new Exception("El PDF se generó, pero no se pudo abrir automáticamente.");
        }
    }
}
