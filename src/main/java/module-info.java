module com.example.clientserver {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.clientserver to javafx.fxml;
    exports com.example.clientserver;
    exports com.example.clientserver.services.servers;
    opens com.example.clientserver.services.servers to javafx.fxml;
    exports com.example.clientserver.controllers;
    opens com.example.clientserver.controllers to javafx.fxml;
    exports com.example.clientserver.services.client;
    opens com.example.clientserver.services.client to javafx.fxml;
}