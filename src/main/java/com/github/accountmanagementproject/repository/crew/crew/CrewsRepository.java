package com.github.accountmanagementproject.repository.crew.crew;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository

public interface CrewsRepository extends JpaRepository<Crew, Long>, CrewsRepositoryCustom {

    @Query("SELECT c FROM Crew c WHERE c.crewMaster.userId = :crewMasterId")
    Crew findByCrewMasterId(@Param("crewMasterId") Long crewMasterId);

    @Query("SELECT c FROM Crew c LEFT JOIN FETCH c.crewImages WHERE c.crewId = :crewId")
    Optional<Crew> findByIdWithImages(@Param("crewId") Long crewId);

    @Query("SELECT DISTINCT c FROM Crew c LEFT JOIN FETCH c.crewImages")
    List<Crew> findAllWithImagesOnly();
}
