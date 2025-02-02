package com.ringme.movie.dto;

import lombok.Data;

import java.util.Set;

@Data
public class Movie {
    private String filmName;
    private Set<Integer> categories;
    private String jpg;
    private String mkv;
    private String srt;
    private String other;

    public void setJpg(String jpg) {
        if(this.jpg == null)
            this.jpg = jpg;
//        else
//            this.jpg += Helper.space + jpg;
    }

    public void setMkv(String mkv) {
        if(this.mkv == null)
            this.mkv = mkv;
//        else
//            this.mkv += Helper.space + mkv;
    }

    public void setSrt(String srt) {
        if(this.srt == null)
            this.srt = srt;
//        else
//            this.srt += Helper.space + srt;
    }

    public void setOther(String other) {
        if(this.other == null)
            this.other = other;
//        else
//            this.other += Helper.space + other;
    }
}
