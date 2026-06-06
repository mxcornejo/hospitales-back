package cl.duoc.hospital.alertas.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ALERTAS")
@Data
@NoArgsConstructor
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PACIENTE_ID", nullable = false)
    private Long pacienteId;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, length = 20)
    private String severidad;

    @Column(length = 500)
    private String descripcion;

    @Column(length = 20)
    private String estado = "ACTIVA";

    @Column(name = "FECHA_HORA")
    private LocalDateTime fechaHora = LocalDateTime.now();
}
