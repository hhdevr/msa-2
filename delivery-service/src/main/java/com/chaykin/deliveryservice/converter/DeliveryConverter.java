package com.chaykin.deliveryservice.converter;

import com.chaykin.common.model.delivery.AddressDto;
import com.chaykin.common.model.delivery.CreateDeliveryRequest;
import com.chaykin.common.model.delivery.DeliveryDto;
import com.chaykin.common.model.delivery.UpdateDeliveryRequest;
import com.chaykin.deliveryservice.persistence.model.Address;
import com.chaykin.deliveryservice.persistence.model.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryConverter {

    DeliveryDto convert(Delivery delivery);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Delivery convert(DeliveryDto dto);

    List<DeliveryDto> convert(List<Delivery> deliveries);

    AddressDto convert(Address address);

    Address convert(AddressDto dto);

    @Mapping(target = "guid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DeliveryDto convert(CreateDeliveryRequest request);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DeliveryDto convert(UpdateDeliveryRequest request);
}
