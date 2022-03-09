package io.jenkins.plugins.datatables.options;

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
