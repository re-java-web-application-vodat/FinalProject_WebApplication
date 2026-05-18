package com.smartlab.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lecturers")

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor

@Builder
public class Lecturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user role lecturer
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // khoa/ngành
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    private String specialization;
}