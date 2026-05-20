package com.smartlab.service;

import com.smartlab.dto.BorrowDTO;
import com.smartlab.entity.Equipment;
import com.smartlab.entity.EquipmentBorrow;
import com.smartlab.entity.User;
import com.smartlab.enums.BorrowStatus;
import com.smartlab.repository.EquipmentBorrowRepository;
import com.smartlab.repository.EquipmentRepository;
import com.smartlab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final EquipmentBorrowRepository borrowRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    public List<Equipment> getAvailableEquipment() {
        return equipmentRepository.findAll().stream()
                .filter(e -> e.getAvailableQuantity() != null && e.getAvailableQuantity() > 0)
                .toList();
    }

    @Transactional
    public void requestBorrow(BorrowDTO dto, String username) {
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new RuntimeException("Số lượng không hợp lệ");
        }

        User student = userRepository.findByUsername(username).orElseThrow();
        Equipment equipment = equipmentRepository.findById(dto.getEquipmentId()).orElseThrow();

        if (equipment.getAvailableQuantity() < dto.getQuantity()) {
            throw new RuntimeException("Không đủ thiết bị khả dụng");
        }

        EquipmentBorrow borrow = EquipmentBorrow.builder()
                .student(student)
                .equipment(equipment)
                .quantity(dto.getQuantity())
                .borrowDate(LocalDate.now())
                .expectedReturnDate(dto.getExpectedReturnDate())
                .status(BorrowStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        borrowRepository.save(borrow);
    }

    @Transactional(readOnly = true)
    public List<EquipmentBorrow> getAllBorrows() {
        return borrowRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public void exportBorrow(Long borrowId) {
        EquipmentBorrow borrow = borrowRepository.findById(borrowId).orElseThrow();

        if (borrow.getStatus() != BorrowStatus.PENDING) {
            throw new RuntimeException("Chỉ xuất phiếu đang chờ duyệt");
        }

        Equipment equipment = borrow.getEquipment();
        if (equipment.getAvailableQuantity() < borrow.getQuantity()) {
            throw new RuntimeException("Không đủ thiết bị trong kho");
        }

        equipment.setAvailableQuantity(equipment.getAvailableQuantity() - borrow.getQuantity());
        equipmentRepository.save(equipment);

        borrow.setStatus(BorrowStatus.EXPORTED);
        borrowRepository.save(borrow);
    }

    @Transactional
    public void returnBorrow(Long borrowId) {
        EquipmentBorrow borrow = borrowRepository.findById(borrowId).orElseThrow();

        if (borrow.getStatus() != BorrowStatus.EXPORTED) {
            throw new RuntimeException("Chỉ trả thiết bị đã xuất");
        }

        Equipment equipment = borrow.getEquipment();
        equipment.setAvailableQuantity(equipment.getAvailableQuantity() + borrow.getQuantity());
        equipmentRepository.save(equipment);

        borrow.setStatus(BorrowStatus.RETURNED);
        borrow.setActualReturnDate(LocalDate.now());
        borrowRepository.save(borrow);
    }
}
