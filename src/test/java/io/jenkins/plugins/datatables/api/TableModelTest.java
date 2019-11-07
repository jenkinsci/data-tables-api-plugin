package io.jenkins.plugins.datatables.api;

import org.junit.jupiter.api.Test;

import io.jenkins.plugins.datatables.api.TableModel.ColumnDefinitionBuilder;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests the class {@link TableModel}.
 *
 * @author Ullrich Hafner
 */
class TableModelTest {
    @Test
    void shouldCreateTableModelWithSimpleColumns() {
        ColumnDefinitionBuilder builder = new ColumnDefinitionBuilder();
        assertThat(builder.toString()).isEqualTo("[]");

        builder.add("one");
        assertThat(builder.toString()).isEqualTo("[{\"data\": \"one\"}]");

        builder.add("two");
        assertThat(builder.toString()).isEqualTo("[{\"data\": \"one\"},{\"data\": \"two\"}]");
    }

    @Test
    void shouldCreateTableModelWithDetailedColumns() {
        ColumnDefinitionBuilder builder = new ColumnDefinitionBuilder();
        assertThat(builder.toString()).isEqualTo("[]");

        builder.add("one", "integer");
        assertThat(builder.toString()).isEqualTo("[{  \"type\": \"integer\",  \"data\": \"one\",  \"render\": {     \"_\": \"display\",     \"sort\": \"sort\"  }}]");

        builder.add("two", "string");
        assertThat(builder.toString()).isEqualTo("[{  \"type\": \"integer\",  \"data\": \"one\",  \"render\": {     \"_\": \"display\",     \"sort\": \"sort\"  }},"
                + "{  \"type\": \"string\",  \"data\": \"two\",  \"render\": {     \"_\": \"display\",     \"sort\": \"sort\"  }}]");
    }
}

