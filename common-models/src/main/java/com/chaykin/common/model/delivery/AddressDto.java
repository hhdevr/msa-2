package com.chaykin.common.model.delivery;

public record AddressDto(
        String country,
        String city,
        String street,
        String building,
        String apartment,
        String zipCode
) {

}
