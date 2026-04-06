package com.chaykin.deliveryservice.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class Address {

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    private String building;

    private String apartment;

    @Column(name = "zip_code")
    private String zipCode;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Address address = (Address) o;
        return Objects.equals(country, address.country)
               && Objects.equals(city, address.city)
               && Objects.equals(street, address.street)
               && Objects.equals(building, address.building)
               && Objects.equals(apartment, address.apartment)
               && Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, city, street, building, apartment, zipCode);
    }

    @Override
    public String toString() {
        return "Address{" +
               "country='" + country + '\'' +
               ", city='" + city + '\'' +
               ", street='" + street + '\'' +
               ", building='" + building + '\'' +
               ", apartment='" + apartment + '\'' +
               ", zipCode='" + zipCode + '\'' +
               '}';
    }
}
