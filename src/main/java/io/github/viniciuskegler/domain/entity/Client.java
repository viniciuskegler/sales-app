package io.github.viniciuskegler.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "client")
@ToString(callSuper = true)
public class Client extends BaseEntity {

    @Column(name = "name", length = 100)
    private String name;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "client")
    private List<Order> orders;
}
