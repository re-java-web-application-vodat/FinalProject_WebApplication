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
        java.util.List<Department> departments = departmentRepository.findAll();
        Department cntt = departments.size() > 0 ? departments.get(0) : null;
        Department qtkd = departments.size() > 1 ? departments.get(1) : null;
        Department ai   = departments.size() > 3 ? departments.get(3) : null;

        // --- Tài khoản Admin ---
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                    .username("admin")
                    .email("admin@smartlab.edu")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .enable(true)
                    .build());
        }

        // --- Tài khoản Sinh viên ---
        if (!userRepository.existsByUsername("student")) {
            userRepository.save(User.builder()
                    .username("student")
                    .email("student@smartlab.edu")
                    .password(passwordEncoder.encode("student123"))
                    .role(Role.STUDENT)
                    .enable(true)
                    .build());
        }

        // --- Giảng viên 1: Khoa CNTT ---
        if (!userRepository.existsByUsername("lecturer")) {
            User lecturerUser = userRepository.save(User.builder()
                    .username("lecturer")
                    .email("lecturer@smartlab.edu")
                    .password(passwordEncoder.encode("lecturer123"))
                    .role(Role.LECTURER)
                    .enable(true)
                    .build());

            if (cntt != null) {
                lecturerRepository.save(Lecturer.builder()
                        .user(lecturerUser)
                        .department(cntt)
                        .specialization("Lập trình Web")
                        .build());
            }
        }

        // --- Giảng viên 2: Khoa QTKD ---
        if (!userRepository.existsByUsername("lecturer2")) {
            User lecturer2User = userRepository.save(User.builder()
                    .username("lecturer2")
                    .email("lecturer2@smartlab.edu")
                    .password(passwordEncoder.encode("lecturer123"))
                    .role(Role.LECTURER)
                    .enable(true)
                    .build());

            if (qtkd != null) {
                lecturerRepository.save(Lecturer.builder()
                        .user(lecturer2User)
                        .department(qtkd)
                        .specialization("Marketing số")
                        .build());
            }
        }

        // --- Giảng viên 3: Khoa Trí tuệ nhân tạo ---
        if (!userRepository.existsByUsername("lecturer3")) {
            User lecturer3User = userRepository.save(User.builder()
                    .username("lecturer3")
                    .email("lecturer3@smartlab.edu")
                    .password(passwordEncoder.encode("lecturer123"))
                    .role(Role.LECTURER)
                    .enable(true)
                    .build());

            if (ai != null) {
                lecturerRepository.save(Lecturer.builder()
                        .user(lecturer3User)
                        .department(ai)
                        .specialization("Machine Learning")
                        .build());
            }
        }
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
