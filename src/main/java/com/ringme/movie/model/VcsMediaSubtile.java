package com.ringme.movie.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "vcs_media_subtile")
@EntityListeners(AuditingEntityListener.class)
public class VcsMediaSubtile implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "media_id")
    private Integer mediaId;
    @Column(name = "name")
    private String name;
    @Column(name = "language")
    private String language;
    @Column(name = "subtile_path")
    private String subtilePath;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "updated_at")
    private Date updatedAt;
}