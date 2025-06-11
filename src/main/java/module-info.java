module bomberman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires org.testng;

    opens com.bomberman to javafx.fxml;
    exports com.bomberman;
}