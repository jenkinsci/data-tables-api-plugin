/* global jQuery, tableDataProxy */
(function ($) {
    $(document).ready(function () {
        bindTables($);
    });
})(jQuery);

/**
 * Binds all tables that have the class 'data-table' to a new JQuery DataTables instance.
 *
 * @param {Object} $ - JQuery
 */
function bindTables($) {
    const allTables = $('table.data-table');

    allTables.each(function () {
        const table = $(this);
        const id = table.attr('id');

        if (table.length) {
            const dataTable = table.DataTable({
                language: {
                    emptyTable: 'Loading - please wait ...'
                },
                deferRender: true,
                pagingType: 'numbers', // page number button only
                order: [[1, 'asc']], // default order, if not persisted yet
                // FIXME: extract to model
                columnDefs: [{
                    targets: 0, // First column contains details button
                    orderable: false
                }],
                columns: JSON.parse(table.attr('data-columns-definition'))
            });

            // Add event listener for opening and closing details
            table.on('click', 'div.details-control', function () {
                const tr = $(this).parents('tr');
                const row = dataTable.row(tr);

                if (row.child.isShown()) {
                    // This row is already open - close it
                    row.child.hide();
                    tr.removeClass('shown');
                } else {
                    // Open this row
                    row.child($(this).data('description')).show();
                    tr.addClass('shown');
                }
            });

            table.on('becameVisible', function() {
                // Content is loaded on demand: if the active tab shows the table, then content is loaded using Ajax
                if (!table[0].hasAttribute('isLoaded')) {
                    table.attr('isLoaded', 'true');
                    tableDataProxy.getTableRows(id, function (t) {
                        (function ($) {
                            var model = JSON.parse(t.responseObject());
                            dataTable.rows.add(model).draw();
                        })(jQuery);
                    });
                }
            });


        }
    });

    allTables.on('order.dt', function (e) {
        var table = $(e.target);
        var order = table.DataTable().order();
        var id = table.attr('id');
        localStorage.setItem(id + '#orderBy', order[0][0]);
        localStorage.setItem(id + '#orderDirection', order[0][1]);
    });

    /**
     * Restores the order of every table by reading the local storage of the browser.
     * If no order has been stored yet, the table is skipped.
     * Also saves the default length of the number of table columns.
     */
    allTables.each(function () {
        // Restore order
        var id = $(this).attr('id');
        var orderBy = localStorage.getItem(id + '#orderBy');
        var orderDirection = localStorage.getItem(id + '#orderDirection');
        var dataTable = $(this).DataTable();
        if (orderBy && orderDirection) {
            var order = [orderBy, orderDirection];
            try {
                dataTable.order(order).draw();
            } catch (ignore) { // TODO: find a way to determine the number of columns here
                dataTable.order([[1, 'asc']]).draw();
            }
        }
        // Store paging size
        $(this).on('length.dt', function (e, settings, len) {
            localStorage.setItem(id + '#table-length', len);
        });
        var storedLength = localStorage.getItem(id + '#table-length');
        if ($.isNumeric(storedLength)) {
            dataTable.page.len(storedLength).draw();
        }
    });
}
