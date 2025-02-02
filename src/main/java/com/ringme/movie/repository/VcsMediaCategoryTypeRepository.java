package com.ringme.movie.repository;

import com.ringme.movie.model.VcsMediaCategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VcsMediaCategoryTypeRepository extends JpaRepository<VcsMediaCategoryType, Integer> {
}