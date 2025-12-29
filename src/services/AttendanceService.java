package services;

import models.Attendance;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceService {

    private static final String FILE_NAME = "data/attendance.csv";

    public void clockIn(String employeeId) {
        String date = LocalDate.now().toString();
        String time = LocalTime.now().toString();

        Attendance attendance = new Attendance(employeeId, date, time);

        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(attendance.toCSV() + "\n");
            System.out.println("Clock in recorded successfully.");
        }
        catch (IOException e) {
            System.out.println("Error writing attendance file.");
        }
    }
}
