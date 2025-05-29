package com.example.myapplication;

public class RateItem {
    private int id;
    private String cname;
    private float cval;

    public RateItem() {}

    public RateItem(String cname, float cval) {
        this.cname = cname;
        this.cval = cval;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCname() { return cname; }
    public void setCname(String cname) { this.cname = cname; }

    public float getCval() { return cval; }
    public void setCval(float cval) { this.cval = cval; }

    @Override
    public String toString() {
        return "RateItem{" + "id=" + id + ", cname='" + cname + '\'' + ", cval=" + cval + '}';
    }
}

