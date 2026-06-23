package com.revature.ers.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "city_state_postal", schema = "\"ExpenseReimbursementManagementSystem\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityStatePostal {

    @Id
    @Column(name = "postalcode")
    private Integer postalCode;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;
}
