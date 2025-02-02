package com.ringme.movie.repository;

import com.ringme.movie.model.VcsMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface VcsMediaRepository extends JpaRepository<VcsMedia, Integer> {
    @Query(value = """
                select * from vcs_media where belong = 'movie tool' and actived in (9, 12)
                """,
            nativeQuery = true)
    List<VcsMedia> getVcsMediaConvertDone();

    @Modifying
    @Query(value = "UPDATE vcs_media SET belong = 'movie tool sub' WHERE id = :id", nativeQuery = true)
    void updateStatusSub(@Param("id") int id);
}