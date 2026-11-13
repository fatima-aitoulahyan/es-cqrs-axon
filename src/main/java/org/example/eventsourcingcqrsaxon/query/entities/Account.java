package org.example.eventsourcingcqrsaxon.query.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.eventsourcingcqrsaxon.enums.AccountStatus;

import java.time.Instant;
import java.util.Date;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Data @Builder
public class Account {
    @Id
    private String id;
    private double balance;
    private String currency;
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    private Instant createdAt;

}
