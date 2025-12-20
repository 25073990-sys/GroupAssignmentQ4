package models;

public class Attendance {

    private String employeeId;
    private String date;
    private String time;

    public Attendance(String employeeId, String date, String time) {
        this.employeeId = employeeId;
        this.date = date;
        this.time = time;
    }

    public String toCSV() {
        return employeeId + "," + date + "," + time;
    }
}
