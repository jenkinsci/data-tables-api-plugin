package io.jenkins.plugins.datatables.api;

import java.util.List;

import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * Provides a default implementation to create the rows of a given date table.
 *
 * @author Ullrich Hafner
 */
public abstract class DefaultAsyncTableContentProvider implements AsyncTableContentProvider {
    private String toJsonArray(final List<Object> rows) {
        JacksonFacade facade = new JacksonFacade();
        return facade.toJson(rows);
    }

    @Override
    @JavaScriptMethod
    public String getTableRows(final String id) {
        return toJsonArray(getTableModel(id).getRows());
    }
}
