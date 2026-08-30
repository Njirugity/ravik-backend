package net.ravik_cms.ravik_backend.account.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.client.Client;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Accounts extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String type;
    private Double openingBalance;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

}
