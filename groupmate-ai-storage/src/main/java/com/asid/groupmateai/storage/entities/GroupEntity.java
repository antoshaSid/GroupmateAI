package com.asid.groupmateai.storage.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "vector_store_id")
    private String vectorStoreId;
}
