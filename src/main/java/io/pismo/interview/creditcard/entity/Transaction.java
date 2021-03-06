package io.pismo.interview.creditcard.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "operation_type_id", nullable = false)
    private OperationType operationType;

    @Column(name = "amount", nullable = false)
    private double amount;

    @CreatedDate
    @Column(name = "event_date")
    private LocalDateTime eventDate;
}
