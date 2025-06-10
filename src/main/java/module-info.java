module bomberman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;

    opens com.bomberman to javafx.fxml;
    exports com.bomberman;
}