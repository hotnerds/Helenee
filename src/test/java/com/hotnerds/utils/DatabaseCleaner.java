package com.hotnerds.utils;

import org.hibernate.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseCleaner implements InitializingBean {

    @PersistenceContext
    EntityManager entityManager;

    private List<String> tableNames = new ArrayList<>();

    @Override
    public void afterPropertiesSet() {
        entityManager.unwrap(Session.class).doWork(this::extractTableNames);
    }

    private void extractTableNames(Connection conn) throws SQLException {
        ResultSet tables = conn.getMetaData()
                .getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"});

        while(tables.next()) {
            tableNames.add(tables.getString("table_name"));
        }
    }

    @Transactional
    public void clean() {
        entityManager.flush();

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        for(String tableName: tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();

            entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN" + " id RESTART WITH 1").executeUpdate();
        }

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
