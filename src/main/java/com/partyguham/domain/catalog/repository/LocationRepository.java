package com.partyguham.domain.catalog.repository;

import com.partyguham.domain.catalog.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByProvince(String province);
}