package io.jenkins.plugins.datatables;

/**
 * A table cell that provides a {@code display} and {@code sort} property so that a JQuery data table can use different
 * properties to sort and display a column. In order to use such a cell the {@link TableColumn} has to be configured using the
 *
 * @param <T>
 *         the type of the sort column
 */
public class DetailedCell<T> {
    private final String display;
    private final T sort;

    /**
     * Creates a new {@link DetailedCell}.
     *
     * @param display
     *         the value that should be used to display the cell
     * @param sort
     *         the value that should be used to sort the cell
     *
     * @see <a href="https://datatables.net/reference/option/columns.type">DataTables Column Types</a>
     */
    public DetailedCell(final String display, final T sort) {
        this.display = display;
        this.sort = sort;
    }

    public String getDisplay() {
        return display;
    }

    public T getSort() {
        return sort;
    }
}
