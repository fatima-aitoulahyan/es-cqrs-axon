package org.example.eventsourcingcqrsaxon.query.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.eventsourcingcqrsaxon.enums.OperationType;

import java.util.Date;

@Entity
@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class AccountOperation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date createdAt;
    private double amount;
    @Enumerated(EnumType.STRING)
    private OperationType operationType;
    private String currency;
    @ManyToOne
    private Account account;
}
