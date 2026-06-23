package com.revature.ers.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_location", schema = "\"ExpenseReimbursementManagementSystem\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventLocation {

    @Id
    @Column(name = "locationid")
    private Integer locationId;

    @Column(name = "street_number")
    private Integer streetNumber;

    @Column(name = "street_name")
    private String streetName;

    @ManyToOne
    @JoinColumn(name = "postalcode")
    private CityStatePostal cityStatePostal;
}
