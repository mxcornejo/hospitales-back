package cl.duoc.hospital.pacientes.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "PACIENTES")
@Data
@NoArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(unique = true, nullable = false, length = 20)
    private String rut;

    @Column(nullable = false)
    private Integer edad;

    @Column(length = 20)
    private String habitacion;

    @Column(length = 500)
    private String diagnostico;

    @Column(length = 20)
    private String estado = "ESTABLE";

    @Column(name = "FECHA_INGRESO")
    private LocalDateTime fechaIngreso = LocalDateTime.now();
}
