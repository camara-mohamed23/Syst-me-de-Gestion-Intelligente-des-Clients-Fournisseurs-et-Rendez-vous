module org.example.gestion {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.desktop;
    requires com.gluonhq.charm.glisten;
    requires jbcrypt;
    requires jdk.jpackage;

    opens org.example.gestion to javafx.fxml;
    exports org.example.gestion;
    exports org.example.gestion.Login;
    opens org.example.gestion.Login to javafx.fxml;

    // Exporte si tu as besoin d'y accéder depuis d'autres modules
    opens org.example.gestion.fournisseur to javafx.fxml;
    exports org.example.gestion.fournisseur;

    // Exporte si CLIENT
    opens org.example.gestion.Client to javafx.fxml;
    exports org.example.gestion.Client;

    // Exporte si RDV
    opens org.example.gestion.RDV to javafx.fxml;
    exports org.example.gestion.RDV;

}