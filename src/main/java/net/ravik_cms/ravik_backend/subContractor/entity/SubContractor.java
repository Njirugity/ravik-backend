package net.ravik_cms.ravik_backend.subContractor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;
import net.ravik_cms.ravik_backend.projects.Projects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubContractor extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String jobType;
    private String email;
    private String address;
    private String phoneNumber;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;
}
