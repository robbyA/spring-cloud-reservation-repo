package com.example.reservationservice;

import java.util.stream.Stream;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.DefaultAfterRollbackProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.backoff.FixedBackOff;

//import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableDiscoveryClient
@SpringBootApplication
@EnableTransactionManagement
public class ReservationServiceApplication {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private AtomicBoolean shouldFail= new AtomicBoolean(false);
	
	private final ReservationRepository repository;
	
	public ReservationServiceApplication(ReservationRepository repository) {

        this.repository = repository;
    }
	
	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}

    @Transactional
    @Bean
    public Function<Reservation, Reservation> process() {
        return re -> {
            logger.info("Received event={}", re);
            Reservation reservation = new Reservation();
            reservation.setName(re.getReservationName());

            if (shouldFail.get()) {
                shouldFail.set(false);
                throw new RuntimeException("Simulated network error");
            } else {
                //We fail every other request as a test
                shouldFail.set(true);
            }
            logger.info("Saving Reservation={}", reservation);

            Reservation savedReservation = repository.save(reservation);

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

@Component
class SampleDataCLR implements CommandLineRunner{
	private final ReservationRepository reservationRepository;

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
	}
}


@RestController
class ReservationRestController {

    private final ReservationRepository reservationRepository;

    ReservationRestController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/reservations")
    Collection<Reservation> reservations() {
        return this.reservationRepository.findAll();
    }
}

interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Collection<Reservation> findByReservationName(String rn);
}


@Entity
class Reservation {

	public Reservation() {
	}

	public Reservation(String reservationName, String type) {
		this.reservationName = reservationName;
		this.type = type;
	}

	@Override
	public String toString() {
		return "Reservation{" +
				"id=" + id +
				", reservationName='" + reservationName + '\'' +
				", type='" + type + '\'' +
				'}';
	}



	@Id
	@GeneratedValue
	private Long id;
	private String reservationName; //reservation_name
	private String type; 
	
	public Long getId() {
		return id;
	}

	public String getReservationName() {
		return reservationName;
	}

	public void setName(String reservationName) {
		this.reservationName = reservationName;
	}
   
	public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}