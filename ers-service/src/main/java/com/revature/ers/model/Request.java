package com.revature.ers.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "request", schema = "\"ExpenseReimbursementManagementSystem\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requestid")
    private Integer requestId;

    @Column(name = "amount")
    private double amount;

    @Column(name = "eventdate")
    private LocalDate eventDate;

    @ManyToOne
    @JoinColumn(name = "eventlocation")
    private EventLocation eventLocation;

    @Column(name = "requestedevent")
    private String requestedEvent;

    @ManyToOne
    @JoinColumn(name = "requesteruserid")
    private User requester;

    @ManyToOne
    @JoinColumn(name = "statusid")
    private RequestStatus requestStatus;
}
