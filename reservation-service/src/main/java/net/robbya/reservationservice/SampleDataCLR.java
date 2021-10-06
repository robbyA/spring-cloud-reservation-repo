package net.robbya.reservationservice;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import net.robbya.reservationservice.domain.Reservation;
import net.robbya.reservationservice.repository.ReservationRepository;

@Component
public class SampleDataCLR implements CommandLineRunner{
	private final ReservationRepository reservationRepository;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	@Autowired
	public SampleDataCLR(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}

	
	private static Reservation[] arrayOfResv = {
		    new Reservation("Jeff Bezos", "reservation event"), 
		    new Reservation("Bill Gates", "reservation event"), 
		    new Reservation("Mark Zuckerberg","reservation event"), 
		};

	@Override
	public void run(String... strings) throws Exception {
		Stream.of(arrayOfResv).forEach(reservation -> 
		reservationRepository.save(new Reservation(reservation.getReservationName(),reservation.getType())));
		reservationRepository.findAll().forEach(System.out::println);
		logger.debug("Reservation Service running::Port = " + System.getenv("PORT"));
	}
}
