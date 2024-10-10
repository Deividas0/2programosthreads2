package com.example.demo.Paveiksliukai;

public class Paveikslelis {
    private int id;
    private byte[] paveikslelis; // BLOB

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
}
