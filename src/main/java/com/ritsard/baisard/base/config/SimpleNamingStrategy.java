package com.ritsard.baisard.base.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class SimpleNamingStrategy implements PhysicalNamingStrategy {
    private final BaseModuleConfigurer configurer;

    public SimpleNamingStrategy(BaseModuleConfigurer configurer) {
        this.configurer = configurer;
    }

    public Identifier toPhysicalCatalogName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return logicalName;
    }

    public Identifier toPhysicalSchemaName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return logicalName;
    }

    public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        String tableName = this.configurer.toSnakeCase(logicalName.getText());
        return Identifier.toIdentifier(tableName);
    }

    public Identifier toPhysicalSequenceName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return this.toPhysicalTableName(logicalName, jdbcEnvironment);
    }

    public Identifier toPhysicalColumnName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        String columnName = logicalName.getText();
        return Identifier.toIdentifier(this.configurer.toSnakeCase(columnName));
    }
}