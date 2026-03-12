package edu.univ.erp.domain;

public class SectionDetails {

    // data fields
    private int sid;
    private String cc;
    private String ct;
    private String inst;
    private String time;
    private String rm;
    private int cap;

    public SectionDetails(int id, String code, String title, String i, String t, String r, int c) {
        this.sid = id;
        this.cc = code;
        this.ct = title;
        this.inst = i;
        this.time = t;
        this.rm = r;
        this.cap = c;
    }

    // getters
    public int getSectionId() { return sid; }
    public String getCourseCode() { return cc; }
    public String getCourseTitle() { return ct; }
    public String getInstructorName() { return inst; }
    public String getDayTime() { return time; }
    public String getRoom() { return rm; }
    public int getCapacity() { return cap; }

    // for dropdowns
    public String toString() {
        return cc + ": " + ct;
    }
}