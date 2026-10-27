package ch.bzz.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int bookingNumber;

    private LocalDate date;

    private String text;

    @ManyToOne
    private Account debitAccount;

    @ManyToOne
    private Account creditAccount;

    private BigDecimal amount;

    @ManyToOne
    private Project project;
}
