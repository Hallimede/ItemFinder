package com.example.t.model.result;

import java.util.List;

public class Ws {
    int bg;
    List<Cw> cw;
    String slot;

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public List<Cw> getCw() {
        return cw;
    }

    public void setCw(List<Cw> cw) {
        this.cw = cw;
    }
}