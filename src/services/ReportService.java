package services;

import models.Sale;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    // FEATURE: FILTER SALES BY DATE RANGE
    public static List<Sale> filterByDateRange(List<Sale> sales, LocalDate start, LocalDate end) {
        // Stream the list, keep only dates equal to or between start/end
        return sales.stream()
                .filter(s -> !s.getTimestamp().toLocalDate().isBefore(start) &&
                        !s.getTimestamp().toLocalDate().isAfter(end))
                .collect(Collectors.toList());
    }

    // FEATURE: SORTING (Date, Amount, Customer)
    public static void sortSales(List<Sale> sales, String criteria, boolean ascending) {
        int n = sales.size();

        for (int i = 0; i < n - 1; i++) { // no need to sort the last one so -1
            for (int j = 0; j < n - i - 1; j++) {
                Sale s1 = sales.get(j);
                Sale s2 = sales.get(j + 1);
                boolean swap = false;

                //Case 1: Sort by Amount (numbers)
                if (criteria.equalsIgnoreCase("amount")) {
                    if (ascending) {
                        if (s1.getTotal() > s2.getTotal()) swap = true;
                    } else { //descending
                        if (s1.getTotal() < s2.getTotal()) swap = true;
                    }
                }
                //Case 2: Sort by Date (numbers)
                else if (criteria.equalsIgnoreCase("date")) {
                    if (ascending) {
                        if (s1.getTimestamp().isAfter(s2.getTimestamp())) swap = true;
                    } else {
                        if (s1.getTimestamp().isBefore(s2.getTimestamp())) swap = true;
                    }
                }
                //Case 3: Sort by Name (Strings)
                else if (criteria.equalsIgnoreCase("customer")) {
                    int comparison = s1.getCustomerName().compareToIgnoreCase(s2.getCustomerName());
                    if (ascending) {
                        if (comparison > 0) swap = true;
                    } else {
                        if (comparison < 0) swap = true;
                    }
                }
                // SWAP
                if (swap) {
                    sales.set(j, s2);   // put s2 in the first spot
                    sales.set(j + 1, s1); //put s1 in the second spot
                }
            }
        }
    }
//    public static void avgDailyRevenue(List<Sale> salesHistory,String periodType){
//        if (salesHistory.isEmpty()){
//            System.out.println("No sales available for analytics");
//            return;
//        }
//        //Use Tree Map so dates sort automatically
//        double totalRevenue = 0.0;
//        Map<String,Double> periodTotals = new TreeMap<>();
//        DateTimeFormatter week = DateTimeFormatter.ofPattern("w");
//
//        //Go through every sale one by one
//        for (Sale s : salesHistory) {
//            LocalDate currentDate = s.getTimestamp().toLocalDate();
//            double amount = s.getTotal();
//            String key = " ";
//        }
}
