package models;

public class Outlet {
    private String outletCode; // e.g., C60
    private String locationName; // e.g., Mid Valley

    public Outlet(String outletCode, String locationName) {
        this.outletCode = outletCode;
        this.locationName = locationName;
    }

    // Getters
    public String getOutletCode() { return outletCode; }
    public String getLocationName() { return locationName; }

    public String toString() {
        return outletCode + ": " + locationName;
    }
}

