package com.ringme.movie.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class MediaKey implements Serializable {
    private int mediaId;
    private int cateTypeId;
}
