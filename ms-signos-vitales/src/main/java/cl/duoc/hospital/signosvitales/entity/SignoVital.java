package cl.duoc.hospital.signosvitales.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "SIGNOS_VITALES")
@Data
@NoArgsConstructor
public class SignoVital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PACIENTE_ID", nullable = false)
    private Long pacienteId;

    @Column(name = "FRECUENCIA_CARDIACA")
    private Integer frecuenciaCardiaca;

    @Column(name = "PRESION_SISTOLICA")
    private Integer presionSistolica;

    @Column(name = "PRESION_DIASTOLICA")
    private Integer presionDiastolica;

    @Column(name = "SATURACION_OXIGENO")
    private Double saturacionOxigeno;

    @Column(name = "TEMPERATURA")
    private Double temperatura;

    @Column(name = "FECHA_HORA")
    private LocalDateTime fechaHora = LocalDateTime.now();
}
