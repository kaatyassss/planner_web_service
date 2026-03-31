module com.kaatyassss.planner {
    requires jdk.httpserver;
    requires java.sql;

    exports com.kaatyassss.planner.web;
    exports com.kaatyassss.planner.web.db;
    exports com.kaatyassss.planner.web.model;
    exports com.kaatyassss.planner.web.dao;
    exports com.kaatyassss.planner.web.handler;
    exports com.kaatyassss.planner.web.session;
    exports com.kaatyassss.planner.web.util;
}
