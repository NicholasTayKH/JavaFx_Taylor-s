module com.example.ap_assignmentuserinterface {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires spring.beans;
    requires spring.context;
    requires spring.context.support;
    requires spring.boot;
    requires jakarta.mail;
    requires spring.boot.autoconfigure;
    requires langchain4j.open.ai; // must add langchain4j references
    requires langchain4j.core;
    requires langchain4j;
    requires org.apache.logging.log4j; // must add log4j references
    requires org.slf4j; // must add slf4j
    requires java.net.http; // needed if HttpTimeoutException occurs
    requires com.fasterxml.jackson.core; // needed if assistant is null


    opens com.example.ap_assignmentuserinterface to javafx.fxml;
    exports com.example.ap_assignmentuserinterface;
    opens org.example.assignment to javafx.fxml;
    exports org.example.assignment;
    exports org.example.assignment.controller;
    opens org.example.assignment.controller to javafx.fxml;
}