package com.example.demo;

public class Paveiksliukas {
    private int id;
    private byte[] paveikslelis;
    private String url;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getPaveikslelis() {
        return paveikslelis;
    }

    public void setPaveikslelis(byte[] paveikslelis) {
        this.paveikslelis = paveikslelis;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
