package me.foxyg3n.iridiumdungeons.database;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import me.foxyg3n.iridiumdungeons.Dungeon;

public class DatabaseManager {

    private Database database;

    public void registerDatabaseConnection() {
        
        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
            .applySetting("hibernate.connection.url", "jdbc:mysql://127.0.0.1:3306/iridiumdungeons?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf-8")
            .applySetting("hibernate.connection.username", "skyblock")
            .applySetting("hibernate.connection.password", "Blackmc123")
            .applySetting("hibernate.connection.driver_class", "java.sql.Driver")
            .applySetting("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect")
            .applySetting("hibernate.hbm2ddl.auto", "update")
            .applySetting("hibernate.show_sql", "false")
            .applySetting("hibernate.c3p0.min_size", "5")
            .applySetting("hibernate.c3p0.max_size", "20")
            .applySetting("hibernate.c3p0.timeout", "1800")
            .applySetting("hibernate.c3p0.max_statements", "50")
            .applySetting("hibernate.connection.CharSet", "utf8")
            .applySetting("hibernate.connection.characterEncoding", "utf8")
            .applySetting("hibernate.connection.useUnicode", "true")
            .build();

        MetadataSources sources = new MetadataSources(standardRegistry);
        sources.addAnnotatedClass(Dungeon.class);
        Metadata metadata = sources.buildMetadata();

        SessionFactory sessionFactory = metadata.buildSessionFactory();
        this.database = new Database(sessionFactory);
    }

    public Database getDatabase() {
        return database;
    }

}