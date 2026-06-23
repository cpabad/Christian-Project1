package com.revature.ers.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "request_status", schema = "\"ExpenseReimbursementManagementSystem\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestStatus {

    @Id
    @Column(name = "statusid")
    private Integer statusId;

    @Column(name = "status")
    private String status;
}
