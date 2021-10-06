package net.robbya.reservationservice.controller;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.DefaultAfterRollbackProcessor;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.robbya.reservationservice.domain.Reservation;
import net.robbya.reservationservice.repository.*;


@RestController
public class ReservationController {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private final ReservationRepository reservationRepository;
	private AtomicBoolean shouldFail= new AtomicBoolean(false);
	
    
	ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/reservations")
    Collection<Reservation> reservations() {
    	logger.debug("Get /reservations method::Port = " +  System.getenv("PORT"));
      	return this.reservationRepository.findAll();
    }

    @Transactional
    @Bean
    public Function<Reservation, Reservation> process() {
        return re -> {
            logger.info("Received event={}", re.toString());
            Reservation reservation = new Reservation();
            reservation.setName(re.getReservationName());
        	reservation.setType(re.getType());
            if (shouldFail.get()) {
                shouldFail.set(false);
                throw new RuntimeException("Simulated network error");
            } else {
                //We fail every other request as a test
                shouldFail.set(true);
            }
            logger.info("Saving Reservation={}", reservation);

            Reservation savedReservation = reservationRepository.save(reservation);

            Reservation event = new Reservation();
            event.setName(savedReservation.getReservationName());
            event.setType("ReservationSaved");
            logger.info("Sent event={}", event);
            return event;
        };
    }



    @Bean
    public ListenerContainerCustomizer<AbstractMessageListenerContainer<byte[], byte[]>> customizer() {
        // Disable retry in the AfterRollbackProcessor
        return (container, destination, group) -> container.setAfterRollbackProcessor(
                new DefaultAfterRollbackProcessor<byte[], byte[]>(
                        (record, exception) -> System.out.println("Discarding failed record: " + record),
                        new FixedBackOff(0L, 0)));
    }




}

