package com.smartlab.repository;

import com.smartlab.entity.BorrowSlipItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowSlipItemRepository extends JpaRepository<BorrowSlipItem, Long> {

    /** Lấy tất cả dòng thiết bị của một phiếu mượn */
    List<BorrowSlipItem> findByBorrowSlipId(Long borrowSlipId);
}
