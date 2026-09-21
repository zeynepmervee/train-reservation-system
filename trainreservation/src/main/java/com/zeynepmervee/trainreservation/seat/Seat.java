package com.zeynepmervee.trainreservation.seat;

import com.zeynepmervee.trainreservation.wagon.Wagon;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "seats",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_seats_wagon_number",
                columnNames = {"wagon_id", "seat_number"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wagon_id", nullable = false)
    private Wagon wagon;

    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;

    @Column(name = "is_window", nullable = false)
    private boolean window;

    public Seat(Wagon wagon, String seatNumber, boolean window) {
        this.wagon = wagon;
        this.seatNumber = seatNumber;
        this.window = window;
    }
}