/* global jQuery3, luxon, tableDataProxy, bootstrap5 */
jQuery3(document).ready(function () {
    /**
     * Binds all tables that have the class 'data-table' to a new JQuery DataTables instance.
     */
    function bindTables($) {
        const OPTIMAL_LABEL = 'Optimal';
        const OTHER_PAGE_LENGTHS = [25, 50, 100];
        const MINIMUM_ROWS = 5;
        const ESTIMATED_ROW_HEIGHT = 42; // used until the first row has been rendered
        const BOTTOM_MARGIN = 16;

        /**
         * Computes the number of rows that fit into the browser window without scrolling the page. If the table
         * starts in the upper half of the window, then the rows should fit below the current position of the table
         * body. Otherwise, the table is far down the page, and the user needs to scroll anyway: then the rows should
         * fit into a whole window together with the controls and the header of the table.
         *
         * @param {jQuery} table the table element
         * @param {Element} container the element that contains the table and its controls
         * @param {Number} rowHeight the height of a single row
         * @return {Number} the number of rows
         */
        function computeOptimalPageLength(table, container, rowHeight) {
            const containerBounds = container.getBoundingClientRect();
            const tableBounds = table[0].getBoundingClientRect();
            const body = table.find('tbody')[0];
            const bodyTop = body ? body.getBoundingClientRect().top : tableBounds.bottom;
            const bodyBottom = body ? body.getBoundingClientRect().bottom : tableBounds.bottom;
            const controlsBelow = Math.max(0, containerBounds.bottom - bodyBottom);

            const bodyTopInPage = bodyTop + window.scrollY;
            const top = bodyTopInPage <= window.innerHeight / 2 ? bodyTopInPage : bodyTop - containerBounds.top;
            const available = window.innerHeight - top - controlsBelow - BOTTOM_MARGIN;

            return Math.max(MINIMUM_ROWS, Math.floor(available / rowHeight));
        }

        /**
         * Adds the page length option 'Optimal' to the configuration of a table. The option is the first entry of the
         * page length menu, its value is computed from the size of the browser window. Whether the user selected this
         * option is stored in the saved state of the table.
         *
         * @param {Object} configuration the configuration of the table
         * @param {Object} optimal the state of the option, will be updated
         */
        function configureOptimalPageLength(configuration, optimal) {
            configuration.lengthMenu = [{label: OPTIMAL_LABEL, value: optimal.length}].concat(OTHER_PAGE_LENGTHS);
            configuration.pageLength = optimal.length;
            configuration.stateSaveParams = function (_settings, data) {
                data.optimalPageLength = optimal.selected;
            };
            configuration.stateLoadParams = function (_settings, data) {
                if (data.optimalPageLength === false
                        || (data.optimalPageLength === undefined && OTHER_PAGE_LENGTHS.includes(data.length))) {
                    optimal.selected = false; // keep a fixed page length the user selected before
                }
                else {
                    data.length = optimal.length; // a saved length might have been computed for another window
                }
            };
        }

        /**
         * Returns the select box of the page length menu of the specified table.
         *
         * @param {Object} dataTable the DataTables API instance
         * @return {jQuery} the select box
         */
        function getPageLengthSelect(dataTable) {
            return $(dataTable.table().container()).find('.dt-length select');
        }

        /**
         * Shows the selected page length in the select box. Since the computed value of the option 'Optimal' might be
         * equal to the value of another option, the option is selected by its index rather than by its value.
         *
         * @param {Object} dataTable the DataTables API instance
         * @param {Object} optimal the state of the option
         */
        function showPageLength(dataTable, optimal) {
            const select = getPageLengthSelect(dataTable);
            if (optimal.selected) {
                select.prop('selectedIndex', 0);
            }
            else {
                const index = OTHER_PAGE_LENGTHS.indexOf(dataTable.page.len());
                if (index >= 0) {
                    select.prop('selectedIndex', index + 1);
                }
            }
        }

        /**
         * Tracks whether the user selects the option 'Optimal' (always the first option) of the page length menu.
         *
         * @param {Object} dataTable the DataTables API instance
         * @param {Object} optimal the state of the option, will be updated
         */
        function trackOptimalPageLength(dataTable, optimal) {
            // Use the capture phase so that the selection is evaluated before DataTables changes the page length
            getPageLengthSelect(dataTable)[0].addEventListener('change', function () {
                optimal.selected = this.selectedIndex === 0;
            }, true);
            dataTable.on('length', function () {
                showPageLength(dataTable, optimal);
            });
            showPageLength(dataTable, optimal);
        }

        /**
         * Computes the page length again using the actual height of the rendered rows. This requires that the table
         * is visible and contains data.
         *
         * @param {jQuery} table the table element
         * @param {Object} dataTable the DataTables API instance
         * @param {Object} optimal the state of the option, will be updated
         */
        function updateOptimalPageLength(table, dataTable, optimal) {
            // The height of the body divided by the number of rows also covers the spacing between the rows
            const rows = table.find('tbody tr');
            const body = table.find('tbody')[0];
            const rowHeight = rows.length > 0 ? body.getBoundingClientRect().height / rows.length : 0;
            if (rowHeight === 0) {
                return;
            }
            optimal.length = computeOptimalPageLength(table, dataTable.table().container(), rowHeight);

            getPageLengthSelect(dataTable).find('option').first().val(optimal.length);
            if (optimal.selected) {
                dataTable.page.len(optimal.length).draw(false);
            }
            showPageLength(dataTable, optimal);
        }

        /**
         * Creates the data table instance for the specified table element.
         */
        function createDataTable(table) {
            const toolbar = $(table).next();
            let toolbarContent;
            let bottom;
            if (toolbar.length && toolbar.children().length > 0) {
                toolbarContent = toolbar;
                bottom = ['pageLength', 'info'];
            }
            else {
                toolbarContent = 'pageLength';
                bottom = 'info';
            }
            const defaultConfiguration = {
                layout: {
                    topStart: toolbarContent,
                    topEnd: 'search',
                    bottomStart: bottom,
                    bottomEnd: 'paging'
                },
                stateSave: true,
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
                            return data;
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
            const useOptimalPageLength = tableConfiguration.optimalPageLength === true;
            delete tableConfiguration.optimalPageLength;

            // overwrite/merge the default configuration with values from the provided table configuration
            const mergedConfiguration = Object.assign(defaultConfiguration, tableConfiguration);
            const optimal = {selected: true, length: 10};
            if (useOptimalPageLength) {
                optimal.length = computeOptimalPageLength(table, table.parent()[0], ESTIMATED_ROW_HEIGHT);
                configureOptimalPageLength(mergedConfiguration, optimal);
            }
            const dataTable = table.DataTable(mergedConfiguration);
            if (useOptimalPageLength) {
                trackOptimalPageLength(dataTable, optimal);
                table.data('optimalPageLength', optimal);
            }
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

                        const optimal = table.data('optimalPageLength');
                        if (optimal) {
                            updateOptimalPageLength(table, dataTable, optimal);
                        }

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
                // Re-enable tooltips
                table.find('[data-bs-toggle="tooltip"]').each(function () {
                    const tooltip = new bootstrap5.Tooltip($(this)[0]);
                    tooltip.enable();
                });

                // Re-draw details icon state
                dataTable.rows().every(function (rowIndex, tableLoop, rowLoop) {
                    const tr = $(this.node());
                    const openRowButton = tr.find('.details-icon-open');
                    const closeRowButton = tr.find('.details-icon-close');

                    if (this.child.isShown()) {
                        openRowButton.hide();
                        closeRowButton.show();
                    }
                    else {
                        openRowButton.show();
                        closeRowButton.hide();
                    }
                } );
            });

            if (table.is(":visible")) {
                loadTableData(table, dataTable);
            }
            else {
                table.on('becameVisible', function () {
                    loadTableData(table, dataTable);
                });
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
