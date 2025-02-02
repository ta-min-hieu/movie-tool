package com.ringme.movie.model;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "vcs_media_category_type")
@EntityListeners(AuditingEntityListener.class)
@IdClass(MediaKey.class)
public class VcsMediaCategoryType implements Serializable {
    @Id
    @Column(name = "media_id")
    private int mediaId;
    @Id
    @Column(name = "cate_type_id")
    private int cateTypeId;
}