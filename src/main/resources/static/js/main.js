// Main JavaScript file for the Ecole Management Application

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Initialize popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
    
    // Form validation
    var forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
    
    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert-dismissible');
        alerts.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);
    
    // Calculate sum for equipment form
    var nbrInput = document.getElementById('nbr');
    var prixInput = document.getElementById('prix_unitaire');
    var sommeInput = document.getElementById('somme');
    
    if (nbrInput && prixInput && sommeInput) {
        function calculateSum() {
            var nbr = parseFloat(nbrInput.value) || 0;
            var prix = parseFloat(prixInput.value) || 0;
            sommeInput.value = (nbr * prix).toFixed(2);
        }
        
        nbrInput.addEventListener('input', calculateSum);
        prixInput.addEventListener('input', calculateSum);
        
        // Calculate on page load
        calculateSum();
    }
    
    // Add sorting functionality to tables
    var tables = document.querySelectorAll('.sortable');
    tables.forEach(function(table) {
        var headers = table.querySelectorAll('th[data-sort]');
        
        headers.forEach(function(header) {
            header.addEventListener('click', function() {
                var column = this.dataset.sort;
                var direction = this.classList.contains('asc') ? 'desc' : 'asc';
                
                // Remove sort classes from all headers
                headers.forEach(function(h) {
                    h.classList.remove('asc', 'desc');
                });
                
                // Add sort class to clicked header
                this.classList.add(direction);
                
                // Sort the table
                sortTable(table, column, direction);
            });
        });
    });
    
    function sortTable(table, column, direction) {
        var tbody = table.querySelector('tbody');
        var rows = Array.from(tbody.querySelectorAll('tr'));
        
        rows.sort(function(a, b) {
            var aValue = a.querySelector('td[data-sort="' + column + '"]').textContent.trim();
            var bValue = b.querySelector('td[data-sort="' + column + '"]').textContent.trim();
            
            // Check if values are numbers
            if (!isNaN(aValue) && !isNaN(bValue)) {
                aValue = parseFloat(aValue);
                bValue = parseFloat(bValue);
            }
            
            if (direction === 'asc') {
                return aValue > bValue ? 1 : -1;
            } else {
                return aValue < bValue ? 1 : -1;
            }
        });
        
        // Clear table and add sorted rows
        while (tbody.firstChild) {
            tbody.removeChild(tbody.firstChild);
        }
        
        rows.forEach(function(row) {
            tbody.appendChild(row);
        });
    }
    
    // Print function
    window.printContent = function() {
        window.print();
    };
    
    // Open equipment print page
    window.printEquipments = function() {
        window.open('/equipements/print', '_blank');
    };
    
    // Date range filter for tables
    var dateFrom = document.getElementById('dateFrom');
    var dateTo = document.getElementById('dateTo');
    var filterBtn = document.getElementById('filterDates');
    
    if (dateFrom && dateTo && filterBtn) {
        filterBtn.addEventListener('click', function() {
            filterTableByDateRange();
        });
    }
    
    function filterTableByDateRange() {
        var from = new Date(dateFrom.value);
        var to = new Date(dateTo.value);
        
        if (isNaN(from.getTime()) || isNaN(to.getTime())) {
            alert('Veuillez sélectionner des dates valides');
            return;
        }
        
        var table = document.querySelector('.table-filterable');
        var rows = table.querySelectorAll('tbody tr');
        
        rows.forEach(function(row) {
            var dateCell = row.querySelector('td[data-date]');
            if (dateCell) {
                var rowDate = new Date(dateCell.dataset.date);
                
                if (rowDate >= from && rowDate <= to) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            }
        });
    }
});

// Confirmation for delete actions
function confirmDelete(url, name) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ' + name + ' ?')) {
        window.location.href = url;
    }
    return false;
}