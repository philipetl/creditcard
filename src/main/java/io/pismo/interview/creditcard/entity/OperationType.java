package io.pismo.interview.creditcard.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "operations_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_type_id")
    @JsonProperty("operation_type_id")
    private Long operationTypeId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "allow_negative", nullable = false)
    private Boolean allowNegative;

    @Column(name = "allow_positive", nullable = false)
    private Boolean allowPositive;

}
