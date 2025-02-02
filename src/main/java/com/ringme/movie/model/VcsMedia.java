package com.ringme.movie.model;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "vcs_media")
@EntityListeners(AuditingEntityListener.class)
public class VcsMedia implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "cate_id")
    private Integer cateId;
    @Column(name = "media_title")
    private String mediaTitle;
    @Column(name = "media_desc")
    private String mediaDesc;
    @Column(name = "media_path")
    private String mediaPath;
    @Column(name = "path")
    private String path;
    @Column(name = "subtile_path")
    private String subtilePath;
    @Column(name = "media_image")
    private String mediaImage;
    @Column(name = "image_thumb")
    private String imageThumb;
    @Column(name = "image_small")
    private String imageSmall;
    @Column(name = "media_time")
    private Integer mediaTime;
    @Column(name = "actived")
    private Integer actived;
//    @Column(name = "is_new")
//    private Integer isNew;
//    @Column(name = "is_hot")
//    private Integer isHot;
    @Column(name = "belong")
    private String belong;
    //TODO chuyen dung dinh dang java.util.Date
    @Column(name = "publish_time")
    private String publishTime;
    //TODO chuyen dung dinh dang java.util.Date
    @Column(name = "first_publish_time")
    private String firstPublishTime;
//    @Column(name = "total_views")
//    private Integer totalViews;
//    @Column(name = "total_likes")
//    private Integer totalLikes;
//    @Column(name = "total_shares")
//    private Integer totalShares;
//    @Column(name = "total_comments")
//    private Integer totalComments;
    @Column(name = "slug")
    private String slug;
    @Column(name = "has_live")
    private Integer hasLive;
    @Column(name = "resolution")
    private Integer resolution;
    @Column(name = "aspect_ratio")
    private String aspectRatio;
    @Column(name = "is_adaptive")
    private Integer isAdaptive;
    @Column(name = "adaptive_path")
    private String adaptivePath;
    @Column(name = "adaptive_resolution")
    private String adaptiveResolution;
//    @Column(name = "selected_profile")
//    private String selectedProfile;
    @Column(name = "encode_type")
    private Integer encodeType;
    @Column(name = "note")
    private String note;
    @Column(name = "category_type_id")
    private Integer categoryTypeId;
    @Column(name = "is_episode")
    private Integer isEpisode;
    @Column(name = "episode_parent")
    private Integer episodeParent;
    @Column(name = "episode_number")
    private Integer episodeNumber;
    @Column(name = "director_id")
    private Integer directorId;
    @Column(name = "actor_id")
    private Integer actorId;
    @Column(name = "producer_id")
    private Integer producerId;
    //TODO chuyen dung dinh dang java.util.Date
    @Column(name = "production_time")
    private String productionTime;
    @Column(name = "approved_by")
    private Long approvedBy;
    //TODO chuyen dung dinh dang java.util.Date
    @Column(name = "approved_at")
    private String approvedAt;
    @Column(name = "is_copyright")
    private Integer isCopyright;
    @Column(name = "copyright_id")
    private Integer copyrightId;
    @Column(name = "check_pod")
    private Integer checkPod;
    @Column(name = "id_sync")
    private Integer idSync;
    @Column(name = "type_sync")
    private String typeSync;
}