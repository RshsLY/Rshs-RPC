package com.rshs.use.api;

import lombok.Data;

import java.io.Serializable;

@Data
public class Rshs implements Serializable {
    private String str;
    private Integer i;

    public Rshs(String str, Integer i) {
        this.str = str;
        this.i = i;
    }
}
