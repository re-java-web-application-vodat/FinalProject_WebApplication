package com.smartlab.config;

import com.smartlab.entity.Department;
import com.smartlab.entity.Equipment;
import com.smartlab.entity.Lecturer;
import com.smartlab.entity.User;
import com.smartlab.enums.Role;
import com.smartlab.repository.DepartmentRepository;
import com.smartlab.repository.EquipmentRepository;
import com.smartlab.repository.LecturerRepository;
import com.smartlab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final LecturerRepository lecturerRepository;
    private final EquipmentRepository equipmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedDepartments();
        seedUsers();
        seedEquipment();
    }

    private void seedDepartments() {
        if (departmentRepository.count() > 0) {
            return;
        }
        departmentRepository.save(Department.builder().name("Công nghệ thông tin").build());
        departmentRepository.save(Department.builder().name("Quản trị kinh doanh").build());
        departmentRepository.save(Department.builder().name("Thiết kế đồ họa").build());
        departmentRepository.save(Department.builder().name("Trí tuệ nhân tạo").build());
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            return;
        }

        Department cntt = departmentRepository.findAll().get(0);

        userRepository.save(User.builder()
                .username("admin")
                .email("admin@smartlab.edu")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .enable(true)
                .build());

        userRepository.save(User.builder()
                .username("student")
                .email("student@smartlab.edu")
                .password(passwordEncoder.encode("student123"))
                .role(Role.STUDENT)
                .enable(true)
                .build());

        User lecturerUser = userRepository.save(User.builder()
                .username("lecturer")
                .email("lecturer@smartlab.edu")
                .password(passwordEncoder.encode("lecturer123"))
                .role(Role.LECTURER)
                .enable(true)
                .build());

        lecturerRepository.save(Lecturer.builder()
                .user(lecturerUser)
                .department(cntt)
                .specialization("Lập trình Web")
                .build());
    }

    private void seedEquipment() {
        if (equipmentRepository.count() > 0) {
            return;
        }

        equipmentRepository.save(Equipment.builder()
                .name("Máy ảnh DSLR")
                .description("Canon EOS cho studio")
                .quantity(5)
                .availableQuantity(5)
                .build());

        equipmentRepository.save(Equipment.builder()
                .name("Máy chiếu")
                .description("Epson Full HD")
                .quantity(10)
                .availableQuantity(10)
                .build());

        equipmentRepository.save(Equipment.builder()
                .name("Laptop Dev")
                .description("Dell XPS cho lập trình")
                .quantity(8)
                .availableQuantity(8)
                .build());
    }
}
