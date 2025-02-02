package com.ringme.movie.repository;

import com.ringme.movie.model.VcsMediaSubtile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface VcsMediaSubtileRepository extends JpaRepository<VcsMediaSubtile, Integer> {
}