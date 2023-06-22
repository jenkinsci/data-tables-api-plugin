package io.jenkins.plugins.datatables;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Provides a configuration for the whole DataTable. This is merged with a default configuration in table.js.
 *
 * @author Andreas Pabst
 */
public class TableConfiguration {
    private final Map<String, Object> configuration = new HashMap<>();

    private boolean useResponsive = false;
    private boolean useColReorder = false;
    private boolean useButtons = false;
    private boolean useSelect = false;
    private boolean useStateSave = false;
    private boolean usePaging = true;

    /**
     * Make the table responsive, i.e. the columns wrap over to a child column.
     *
     * @return this {@link TableConfiguration} for chaining methods
     *
     * @see <a href="https://datatables.net/extensions/responsive/">https://datatables.net/extensions/responsive/</a>
     */
    public TableConfiguration responsive() {
        configuration.put("responsive", true);
        useResponsive = true;
        return this;
    }

    /**
     * Returns whether the responsive extension was configured to be used.
     *
     * @return true, if the responsive extension should be used, false otherwise
     */
    public boolean isUseResponsive() {
        return useResponsive;
    }

    /**
     * Enable reordering of table columns.
     *
     * @return this {@link TableConfiguration} for chaining methods
     *
     * @see <a href="https://datatables.net/extensions/colreorder/">https://datatables.net/extensions/colreorder/</a>
     */
    public TableConfiguration colReorder() {
        configuration.put("colReorder", true);
        useColReorder = true;
        return this;
    }

    /**
     * Returns whether column reordering was configured to be used.
     *
     * @return true, if column reordering should be used, false otherwise
     */
    public boolean isUseColReorder() {
        return useColReorder;
    }

    /**
     * Enable the default buttons.
     *
     * @return this {@link TableConfiguration} for chaining methods
     *
     * @see <a href="https://datatables.net/extensions/buttons/">https://datatables.net/extensions/buttons/</a>
     */
    public TableConfiguration buttons() {
        configuration.put("buttons", true);
        useButtons = true;
        return this;
    }

    /**
     * Enable specific buttons.
     *
     * @param types
     *         List of buttons to use
     *
     * @return this {@link TableConfiguration} for chaining methods
     *
     * @see <a href="https://datatables.net/extensions/buttons/built-in">https://datatables.net/extensions/buttons/built-in</a>
     */
    public TableConfiguration buttons(final String... types) {
        configuration.put("buttons", types);
        useButtons = true;
        return this;
    }

    /**
     * Returns whether buttons were configured to be used.
     *
     * @return true, if buttons should be used, false otherwise
     */
    public boolean isUseButtons() {
        return useButtons;
    }

    /**
     * Enable selection.
     *
     * @param selectStyle
     *          The {@link SelectStyle selection style}
     *
     * @return this {@link TableConfiguration} for chaining methods
     *
     * @see <a href="https://datatables.net/reference/option/select">https://datatables.net/reference/option/select</a>
     */
    public TableConfiguration select(final SelectStyle selectStyle) {
        configuration.put("select", selectStyle.getStyle());
        useSelect = true;
        return this;
    }

    /**
     * Returns whether select is configured to be used.
     *
     * @return true, if select should be used, false otherwise
     */
    public boolean isUseSelect() {
        return useSelect;
    }

    /**
     * Enable saving of the table state.
     *
     * @return this {@link TableConfiguration} for chaining methods
     *
     * @see <a href="https://datatables.net/reference/option/stateSave">https://datatables.net/reference/option/stateSave</a>
     */
    public TableConfiguration stateSave() {
        configuration.put("stateSave", true);
        useStateSave = true;
        return this;
    }

    /**
     * Returns whether stateSave is configured to be used.
     *
     * @return true, if stateSave should be used, false otherwise
     */
    public boolean isUseStateSave() {
        return useStateSave;
    }

    /**
     * Disable paging.
     *
     * @return this {@link TableConfiguration} for chaining methods
     *
     * @see <a href="https://datatables.net/reference/option/paging">https://datatables.net/reference/option/paging</a>
     */
    public TableConfiguration noPaging() {
        configuration.put("paging", false);
        usePaging = false;
        return this;
    }

    /**
     * Returns whether paging is configured to be used.
     *
     * @return true, if paging should be used, false otherwise.
     */
    public boolean isUsePaging() {
        return usePaging;
    }

    /**
     * Get the configuration as JSON.
     *
     * @return a JSON Object with the configuration
     */
    public String getConfiguration() {
        try {
            return new ObjectMapper().writeValueAsString(configuration);
        }
        catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    String.format("Can't convert table configuration '%s' to JSON object", configuration), exception);
        }
    }

    /**
     * Empty Object to be used when creating the configuration. By default, Jackson refuses to create empty JSON
     * objects, but this can be changed via the @{@link JsonSerialize} annotation.
     */
    @JsonSerialize
    private static class EmptyConfigObject {
    }

    /**
     * The possible values of the <a href="https://datatables.net/reference/option/select.style">select.style</a> option,
     * which is used for enabling the selection for datatables and setting its style (the default is {@link #OS}.
     *
     * @author Florian Orendi
     */
    public enum SelectStyle {
        /**
         * Selection can only be performed via the API.
         */
        API("api"),

        /**
         * Only a single item can be selected, any other selected items will be automatically
         * deselected when a new item is selected.
         */
        SINGLE("single"),

        /**
         * Multiple items can be selected. Selection is performed by simply clicking on the items to be selected.
         */
        MULTI("multi"),

        /**
         * Operating System (OS) style selection.
         * This is the most comprehensive option and provides complex behaviours such as ctrl/cmd clicking
         * to select / deselect individual items, shift clicking to select ranges
         * and an unmodified click to select a single item.
         */
        OS("os"),

        /**
         * A hybrid between the {@link #OS} style and {@link #MULTI},
         * allowing easy multi-row selection without immediate de-selection when clicking on a row.
         */
        MULTI_SHIFT("multi+shift");

        private final String style;

        SelectStyle(final String style) {
            this.style = style;
        }

        public String getStyle() {
            return style;
        }
    }
}
