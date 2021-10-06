package net.robbya.reservationservice.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Reservation {

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
