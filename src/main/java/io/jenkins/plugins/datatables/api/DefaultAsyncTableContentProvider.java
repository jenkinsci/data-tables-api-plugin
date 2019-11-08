package io.jenkins.plugins.datatables.api;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * An {@link AsyncTableContentProvider} that provides a default implementation to extract the rows of a given date table
 * as JSON objects.
 *
 * @author Ullrich Hafner
 */
public abstract class DefaultAsyncTableContentProvider implements AsyncTableContentProvider {
    private String toJsonArray(final List<Object> rows) {
        try {
            return new ObjectMapper().writeValueAsString(rows);
        }
        catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    String.format("Can't convert table rows '%s' to JSON object", rows), exception);
        }
    }

    @Override
    @JavaScriptMethod
    public String getTableRows(final String id) {
        return toJsonArray(getTableModel(id).getRows());
    }
}
