package com.ringme.movie.repository;

import com.ringme.movie.model.VcsMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface VcsMediaRepository extends JpaRepository<VcsMedia, Integer> {
    @Query(value = """
                select * from vcs_media where belong in ('movie tool', 'movie tool sub') and actived in (9, 12)
                """,
            nativeQuery = true)
    List<VcsMedia> getVcsMediaConvertDone();

    @Query(value = """
                select * from vcs_media where belong in ('movie tool anime') and actived in (9, 12)
                """,
            nativeQuery = true)
    List<VcsMedia> getVcsMediaAnimeConvertDone();

    @Query(value = """
                select m.* from vcs_media m
                inner join vcs_media_subtile ms on m.id = ms.media_id
                where ms.language = "en";
                """,
            nativeQuery = true)
    List<VcsMedia> getVcsMediasHaveSubtitleGenerate();

    @Query(value = """
                select * from vcs_media
                where belong in ('movie tool', 'movie tool sub') and actived in (9, 12) and id = :id
                """,
            nativeQuery = true)
    VcsMedia getVcsMediaConvertDoneById(@Param("id") Integer id);

    @Modifying
    @Query(value = "UPDATE vcs_media SET belong = 'movie tool sub' WHERE id = :id", nativeQuery = true)
    void updateStatusSub(@Param("id") int id);

    @Modifying
    @Query(value = "UPDATE vcs_media SET media_time = :media_time WHERE id = :id", nativeQuery = true)
    void updateMediaTimeById(@Param("id") int id,
                             @Param("media_time") int mediaTime);

    @Modifying
    @Query(value = "UPDATE vcs_media SET actived = :status WHERE id = :id", nativeQuery = true)
    void updateStatus(@Param("id") int id, @Param("status") int status);

    @Modifying
    @Query(value = "UPDATE vcs_media SET actived = :status, approved_at = :approved_at WHERE id = :id", nativeQuery = true)
    void updateStatus(@Param("id") int id, @Param("status") int status, @Param("approved_at") Date approvedAt);
}