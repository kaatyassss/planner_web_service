module com.kaatyassss.planner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires jdk.httpserver;
    requires java.sql;

    opens com.kaatyassss.planner to javafx.fxml, javafx.controls;
    exports com.kaatyassss.planner;

    opens com.kaatyassss.planner.controllers to javafx.fxml, javafx.controls;
    exports com.kaatyassss.planner.controllers;

    opens com.kaatyassss.planner.utils to javafx.fxml, javafx.controls;
    exports com.kaatyassss.planner.utils;

    exports com.kaatyassss.planner.web;
    exports com.kaatyassss.planner.web.db;
    exports com.kaatyassss.planner.web.model;
    exports com.kaatyassss.planner.web.dao;
    exports com.kaatyassss.planner.web.handler;
    exports com.kaatyassss.planner.web.session;
    exports com.kaatyassss.planner.web.util;
}