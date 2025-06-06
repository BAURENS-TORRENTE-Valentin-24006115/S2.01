module bomberman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.bomberman to javafx.fxml;
    exports com.bomberman;
}