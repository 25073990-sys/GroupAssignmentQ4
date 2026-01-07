package services;

import models.Sale;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {

    // =========================================================
    // FEATURE: FILTER SALES BY DATE RANGE
    // =========================================================
    public static List<Sale> filterByDateRange(List<Sale> sales, LocalDate start, LocalDate end) {
        // Stream the list, keep only dates equal to or between start/end
        return sales.stream()
                .filter(s -> !s.getTimestamp().toLocalDate().isBefore(start) &&
                        !s.getTimestamp().toLocalDate().isAfter(end))
                .collect(Collectors.toList());
    }

    // =========================================================
    // FEATURE: SORTING (Date, Amount, Customer)
    // =========================================================
    public static void sortSales(List<Sale> sales, String criteria, boolean ascending) {

        switch (criteria.toLowerCase()) {
            case "date":
                // Sort by Timestamp
                sales.sort(Comparator.comparing(Sale::getTimestamp));
                break;

            case "amount":
                // Sort by Total Price (Double)
                sales.sort(Comparator.comparingDouble(Sale::getTotal));
                break;

            case "customer":
                // Sort by Name (String, ignoring Case)
                sales.sort(Comparator.comparing(Sale::getCustomerName, String.CASE_INSENSITIVE_ORDER));
                break;
        }

        // If the user wanted Descending (High->Low or Z->A), we reverse the list
        if (!ascending) {
            Collections.reverse(sales);
        }
    }
}
