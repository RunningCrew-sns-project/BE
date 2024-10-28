package com.github.accountmanagementproject.repository.blogImages;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogImagesRepository extends JpaRepository<BlogImages, Integer> {
}
