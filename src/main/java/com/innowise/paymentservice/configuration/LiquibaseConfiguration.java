package com.innowise.paymentservice.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.ext.mongodb.database.MongoConnection;
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LiquibaseConfiguration {
    @Bean
    public Liquibase liquibase() {

        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("payment");

            MongoConnection connection = new MongoConnection();
            connection.setMongoDatabase(mongoDatabase);

            Database database = new MongoLiquibaseDatabase();
            database.setConnection(connection);

        return new Liquibase("db/changelog/db.changelog-master.yaml",
                new ClassLoaderResourceAccessor(),
                database);

    }
    @Bean
    public CommandLineRunner runLiquibase(Liquibase liquibase) {
        return args -> liquibase.update();
    }
}
