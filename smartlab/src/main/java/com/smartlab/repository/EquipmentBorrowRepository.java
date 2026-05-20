package com.smartlab.repository;

import com.smartlab.entity.EquipmentBorrow;
import com.smartlab.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentBorrowRepository extends JpaRepository<EquipmentBorrow, Long> {

    List<EquipmentBorrow> findByStudent_UsernameOrderByCreatedAtDesc(String username);

    List<EquipmentBorrow> findAllByOrderByCreatedAtDesc();

    List<EquipmentBorrow> findByStatusOrderByCreatedAtDesc(BorrowStatus status);
}
