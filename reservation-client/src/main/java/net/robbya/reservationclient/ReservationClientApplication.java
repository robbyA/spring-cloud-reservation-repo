package com.example.reservationclient;

//import org.springframework.beans.factory.annotation.Required;
//import javax.xml.ws.RequestWrapper;
//import org.springframework.cloud.stream.annotation.EnableBinding;
//import org.springframework.cloud.stream.annotation.Output;
//import java.util.StringJoiner;
//import org.springframework.integration.annotation.Gateway;
//import org.springframework.integration.annotation.IntegrationComponentScan;
//import org.springframework.integration.annotation.MessagingGateway;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
//import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.CollectionModel;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.Valid;
import org.springframework.validation.BindingResult;
//@EnableResourceServer
//@IntegrationComponentScan
//@EnableBinding (ReservationChannels.class)
@EnableFeignClients
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
@EnableCircuitBreaker

public class ReservationClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationClientApplication.class, args);
	}
}

@RestController
@RequestMapping("/reservations")
class ReservationApiGateway {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	BlockingQueue<Reservation> unbounded = new LinkedBlockingQueue<>();

	private final ReservationReader reservationReader;
	// private final ReservationWriter reservationWriter;

	@Autowired
	public ReservationApiGateway(ReservationReader reservationReader) {
		this.reservationReader = reservationReader;

	}

	@PostMapping
	public String sendMessage(@Valid @RequestBody Reservation incomming, BindingResult bindingResult) {
		
		if (bindingResult.hasErrors()) {return bindingResult.getAllErrors().toString();}
		logger.info("Received request={}", incomming);
		Reservation reservationEvent = new Reservation();
		reservationEvent.setType("CreateReservation");
		reservationEvent.setReservationName(incomming.getReservationName());
		unbounded.offer(reservationEvent);
		logger.info("reservationEvent sent={}", reservationEvent);
		return "Reservaton sent";

	}

	@Bean
	public Supplier<Reservation> supplier() {
		return () -> unbounded.poll();
	}

	public Collection<String> fallback() {
		return new ArrayList<>();
	}

	
	@HystrixCommand(defaultFallback = "fallback", commandProperties = {
		@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000") })
	@GetMapping("/names")
	// @HystrixCommand(fallbackMethod = "fallback")
	public Collection<String> names() {

		return this.reservationReader
				.read()
				.stream()
				.map(Reservation::getReservationName)
				.collect(Collectors.toList());
	}
}

@FeignClient("reservation-service")
interface ReservationReader {

	@GetMapping("/reservations")
	Collection<Reservation> read();
}

class Reservation {
	
	
	private Long id;
	@NotNull
	@Size(min=5, max=30)
	private String reservationName; // reservation_name
	private String type;

	public Long getId() {
		return id;
	}

	public String getReservationName() {
		return reservationName;
	}

	public void setReservationName(String reservationName) {
		this.reservationName = reservationName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Reservation{" + ", reservationName='" + reservationName + '\'' + ", type='" + type + '\'' + '}';
	}

}
