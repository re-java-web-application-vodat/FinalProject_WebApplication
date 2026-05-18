package com.smartlab.service;

import com.smartlab.entity.Equipment;
import com.smartlab.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository repository;

    public List<Equipment> getAll(){

        return repository.findAll();
    }

    public void create(Equipment equipment){

        repository.save(equipment);
    }

    public Equipment findById(Long id){

        return repository.findById(id)
                .orElseThrow();
    }

    public void update(Equipment equipment){

        repository.save(equipment);
    }

    public void delete(Long id){

        repository.deleteById(id);
    }
}
