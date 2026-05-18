package com.smartlab.config;

import com.smartlab.entity.Department;
import com.smartlab.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DepartmentRepository
            departmentRepository;

    @Override
    public void run(String... args) {

        if(departmentRepository.count() == 0){

            departmentRepository.save(Department.builder().name("Công nghệ thông tin").build());

            departmentRepository.save(Department.builder().name("Quản trị kinh doanh").build());

            departmentRepository.save(Department.builder().name("Thiết kế đồ họa").build());

            departmentRepository.save(Department.builder().name("Trí tuệ nhân tạo").build());
        }
    }
}
