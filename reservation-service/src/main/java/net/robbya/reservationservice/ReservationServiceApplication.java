package net.robbya.reservationservice;


//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.function.Function;
//import javax.transaction.Transactional;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.kafka.listener.AbstractMessageListenerContainer;
//import org.springframework.kafka.listener.DefaultAfterRollbackProcessor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
/*import org.springframework.util.backoff.FixedBackOff;
import com.example.reservationservice.domain.Reservation;
import com.example.reservationservice.repository.ReservationRepository;
*/

@EnableDiscoveryClient
@SpringBootApplication
@EnableTransactionManagement
public class ReservationServiceApplication {

	/*
	 * public ReservationServiceApplication(ReservationRepository repository) {
	 * 
	 * this.repository = repository; }
	 */
	
	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}

}





