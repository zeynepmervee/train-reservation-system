package com.zeynepmervee.trainreservation.wagon;

import com.zeynepmervee.trainreservation.train.Train;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "wagons",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_wagons_train_number",
                columnNames = {"train_id", "wagon_number"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wagon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @Column(name = "wagon_number", nullable = false)
    private int wagonNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "wagon_type", nullable = false, length = 20)
    private WagonType wagonType;

    public Wagon(Train train, int wagonNumber, WagonType wagonType) {
        this.train = train;
        this.wagonNumber = wagonNumber;
        this.wagonType = wagonType;
    }
}