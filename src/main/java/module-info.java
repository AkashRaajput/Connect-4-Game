module com.akash.connectfour {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.logging;

    opens com.akash.connectfour to javafx.fxml;

    exports com.akash.connectfour;
}
