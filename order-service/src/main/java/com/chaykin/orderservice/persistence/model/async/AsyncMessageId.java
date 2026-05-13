package com.chaykin.orderservice.persistence.model.async;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class AsyncMessageId implements Serializable {

    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "topic", nullable = false)
    private String topic;
}
