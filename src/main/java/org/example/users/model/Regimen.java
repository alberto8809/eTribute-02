package org.example.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "regimen")
public class Regimen {
    @Id
    @Column(name = "regimen_id")
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    String regimen_id;
    @Column(name = "description")
    String description;
    @Column(name = "fisica")
    String fisica;
    @Column(name = "moral")
    String moral;
    @Column(name = "start_date")
    String start_date;
    @Column(name = "end_date")
    String end_date;


}
