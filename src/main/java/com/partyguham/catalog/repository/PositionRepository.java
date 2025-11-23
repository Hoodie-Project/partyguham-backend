package com.partyguham.catalog.repository;

import com.partyguham.catalog.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {

    // main 기준 조회
    List<Position> findByMain(String main);
}