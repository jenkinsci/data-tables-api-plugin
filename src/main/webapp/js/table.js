/* global jQuery3, luxon, tableDataProxy, bootstrap5 */
jQuery3(document).ready(function () {
    /**
     * Binds all tables that have the class 'data-table' to a new JQuery DataTables instance.
     */
    function bindTables($) {
        /**
         * Creates the data table instance for the specified table element.
         */
        function createDataTable(table) {
            const defaultConfiguration = {
                stateSave: typeof Prototype !== 'object', // do not save state when Prototype is still loaded
                language: {
                    emptyTable: 'Loading - please wait ...'
                },
                responsive: {
                    details: false
                },
                deferRender: true,
                paging: true,
                pagingType: 'numbers', // page number button only
                order: [[1, 'asc']], // default order, if not persisted yet
                columnDefs: [
                    {
                        targets: 'nosort', // All columns with the '.nosort' class in the <th>
                        orderable: false
                    },
                    {
                        targets: 'text-end', // All columns with the '.text-end' class in the <th>
                        className: 'text-end'
                    },
                    {
                        targets: 'date', // All columns with the '.date' class in the <th>
                        render: function (data, type, _row, _meta) {
                            if (type === 'display') {
                                if (data === 0) {
                                    return '-';
                                }
                                const dateTime = luxon.DateTime.fromMillis(data * 1000);
                                return '<span data-bs-toggle="tooltip" data-bs-placement="top" title="'
                                    + dateTime.toLocaleString(luxon.DateTime.DATETIME_SHORT) + '">'
                                    + dateTime.toRelative({locale: 'en'}) + '</span>';
                            }
                            else {
                                return data;
                            }
                        }
                    },
                    {
                        targets: 'percentage', // All columns with the '.percentage' class in the <th>
                        className: 'text-end',
                        render: function (data, type, _row, _meta) {
                            if (isNaN(data)) {
                                return data;
                            }
                            return Number(data).toLocaleString(undefined,
                                {style: 'percent', minimumFractionDigits: 2});
                        }
                    },
                    {
                        targets: 'hidden', // All columns with the '.hidden' class in the <th>
                        visible: false,
                        searchable: true,
                        orderable: false // There is no point in allowing sort by this column if it's not visible.
                    }
                ],
                columns: JSON.parse(table.attr('data-columns-definition'))
            };
            const tableConfiguration = JSON.parse(table.attr('data-table-configuration'));
            // overwrite/merge the default configuration with values from the provided table configuration
            const mergedConfiguration = Object.assign(defaultConfiguration, tableConfiguration);
            const dataTable = table.DataTable(mergedConfiguration);
            // add the buttons to the top of the table
            if (tableConfiguration.buttons) {
                dataTable
                    .buttons()
                    .container()
                    .addClass('float-none mb-3')
                    .prependTo($(dataTable.table().container()).closest('.table-responsive'));
            }

            return dataTable;
        }

        /**
         * Loads the content for the specified table element via an Ajax call.
         */
        function loadTableData(table, dataTable) {
            if (!table[0].hasAttribute('isLoaded')) {
                table.attr('isLoaded', 'false');
                tableDataProxy.getTableRows(table.attr('id'), function (t) {
                    (function () {
                        const model = JSON.parse(t.responseObject());
                        dataTable.rows.add(model).draw();
                        dataTable.columns.adjust().draw();

                        table.attr('isLoaded', 'true');
                        table.emptyTable = 'No records found';
                        table.find('.details-icon-close').each(function () {
                            $(this).hide();
                        });
                    })();
                });
            }
        }

        const allTables = $('table.data-table');
        allTables.each(function () {
            const table = $(this);
            const id = table.attr('id');
            const dataTable = createDataTable(table);

            // Add event listener for opening and closing details
            table.on('click', 'div.details-control', function () {
                const tr = $(this).parents('tr');
                const row = dataTable.row(tr);

                const openRowButton = tr.find('.details-icon-open');
                const closeRowButton = tr.find('.details-icon-close');
                if (row.child.isShown()) {
                    row.child.hide();
                    tr.removeClass('shown');
                    openRowButton.show();
                    closeRowButton.hide();
                }
                else {
                    row.child($(this).data('description')).show();
                    tr.addClass('shown');
                    openRowButton.hide();
                    closeRowButton.show();
                }
            });

            table.on('draw.dt', function () {
                table.find('[data-bs-toggle="tooltip"]').each(function () {
                    const tooltip = new bootstrap5.Tooltip($(this)[0]);
                    tooltip.enable();
                });
            });

            if (table.is(":visible")) {
                loadTableData(table, dataTable);
            }
            else {
                table.on('becameVisible', function () {
                    loadTableData(table, dataTable);
                });
            }

            // Since Jenkins 2.406 Prototype has been removed from core.
            // So we basically do not need to support a custom serialization of the data-tables state
            // anymore. We can now use the default auto-save functionality of DataTables.
            if (typeof Prototype === 'object') {
                // Add event listener that stores the order a user selects
                table.on('order.dt', function () {
                    const order = table.DataTable().order();
                    localStorage.setItem(id + '#orderBy', order[0][0]);
                    localStorage.setItem(id + '#orderDirection', order[0][1]);
                });

                /**
                 * Restores the order of every table by reading the local storage of the browser.
                 * If no order has been stored yet, the table is skipped.
                 * Also saves the default length of the number of table columns.
                 */
                const orderBy = localStorage.getItem(id + '#orderBy');
                const orderDirection = localStorage.getItem(id + '#orderDirection');
                if (orderBy && orderDirection) {
                    const order = [orderBy, orderDirection];
                    try {
                        dataTable.order(order).draw();
                    }
                    catch (ignore) { // TODO: find a way to determine the number of columns here
                        dataTable.order([[1, 'asc']]).draw();
                    }
                }

                // Store paging size
                table.on('length.dt', function (e, settings, len) {
                    localStorage.setItem(id + '#table-length', len);
                });
                const storedLength = localStorage.getItem(id + '#table-length');
                if ($.isNumeric(storedLength)) {
                    dataTable.page.len(storedLength).draw();
                }

                // Store search text
                if (table.attr('data-remember-search-text') === 'true') {
                    table.on('search.dt', function () {
                        localStorage.setItem(id + '#table-search-text', dataTable.search());
                    });
                    const storedSearchText = localStorage.getItem(id + '#table-search-text');
                    if (storedSearchText) {
                        dataTable.search(storedSearchText).draw();
                    }
                }
            }
        });
    }

    (function ($) {
        $.extend(true, $.fn.dataTable.defaults, {
            mark: {
                className: 'highlight'
            },
            responsive: {
                details: false
            }
        });
        bindTables($);
    })(jQuery3);
});
