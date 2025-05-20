package com.example.places.repository;

import com.example.places.model.places.Place;
import com.example.places.model.places.Shipment;
import com.example.places.model.places.UserPlace;
import com.example.places.utils.enums.ShipmentStatus;
import com.example.places.utils.enums.ShipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    @Query("""
    select s
    from Shipment s
    where (:placeId is null or s.place.id = :placeId)
         and (:status is null or s.status = :status)
         and (:type is null or s.type = :type)
    """)
    List<Shipment> findByPlaceAndTypeAndStatus(Long placeId, ShipmentStatus status, ShipmentType type);

    @Query("""
    select s
    from Shipment s
    where (:placeId is null or s.place.id = :placeId)
         and (:status is null or s.status = :status)
         and (:type is null or s.type = :type)
         and (s.deliveredAt >= :startDate)
         and (s.deliveredAt <= :endDate)
    """)
    List<Shipment> findByPlaceAndTypeAndStatusFilteringByDate(Long placeId, ShipmentStatus status, ShipmentType type, OffsetDateTime startDate, OffsetDateTime endDate);


    Optional<Shipment> findByPhrase(String phrase);
}
