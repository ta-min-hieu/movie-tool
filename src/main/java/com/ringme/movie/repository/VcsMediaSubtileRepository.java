package com.ringme.movie.repository;

import com.ringme.movie.model.VcsMediaSubtile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface VcsMediaSubtileRepository extends JpaRepository<VcsMediaSubtile, Integer> {
    @Query(value = """
        select a from VcsMediaSubtile a where a.mediaId = :mediaId and a.language = :language
        """)
    VcsMediaSubtile findByMediaIdAndLanguage(@Param("mediaId") int mediaId,
                                             @Param("language") String language);
}