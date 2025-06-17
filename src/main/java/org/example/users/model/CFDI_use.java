package org.example.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "CFDI_use")
public class CFDI_use {
    @Id
    @Column(name = "CFDI_use_id")
    private String CFDI_use_id;
    @Column(name = "description")
    private String description;
    @Column(name = "person_type")
    private String person_type;
    @Column(name = "start_date")
    private String start_date;
    @Column(name = "end_date")
    private String end_date;
    @Column(name = "regimen")
    private String regimen;
    @Column(name = "person_type_")
    private String person_type_;

}
