package com.asid.groupmateai.storage.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Entity
@Table(name = "GROUPS")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "vector_store_id", unique = true, nullable = false)
    private String vectorStoreId;

    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
    private Collection<GroupUserEntity> groupUsers;
}
