package net.robbya.reservationservice.repository;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

import net.robbya.reservationservice.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Collection<Reservation> findByReservationName(String rn);
}
