module com.cgvsu {
    requires javafx.controls;
    requires javafx.fxml;
    requires vecmath;
    requires java.desktop;
    requires rt;


    opens com.cgvsu to javafx.fxml;
    exports com.cgvsu;
}