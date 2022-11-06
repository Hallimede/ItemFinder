package com.example.t.model.result;

import java.util.List;

public class Result {
    int sn;
    boolean ls;
    int bg;
    int ed;
    List<Ws> ws;
    int sc;

    public int getSc() {
        return sc;
    }

    public void setSc(int sc) {
        this.sc = sc;
    }

    public List<Ws> getWs() {
        return ws;
    }

    public void setWs(List<Ws> ws) {
        this.ws = ws;
    }
}