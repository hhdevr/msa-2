package com.chaykin.deliveryservice.service.model;

public record AddressDto(
        String country,
        String city,
        String street,
        String building,
        String apartment,
        String zipCode
) {

}
