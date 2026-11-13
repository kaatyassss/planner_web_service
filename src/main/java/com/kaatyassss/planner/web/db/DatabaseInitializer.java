package com.kaatyassss.planner.web.db;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

// Spring создаёт компонент и выполняет инициализацию БД до завершения запуска приложения
@Component
public class DatabaseInitializer implements InitializingBean {

    // Вызывается Spring после создания компонента; используем существующие SQL-команды
    @Override
    public void afterPropertiesSet() {
        DbInit.createTables();
    }
}
