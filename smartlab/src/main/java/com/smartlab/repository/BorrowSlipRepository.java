package com.smartlab.repository;

import com.smartlab.entity.BorrowSlip;
import com.smartlab.enums.BorrowSlipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowSlipRepository extends JpaRepository<BorrowSlip, Long> {

    /** Lấy phiếu mượn của sinh viên theo student_id, mới nhất trước */
    List<BorrowSlip> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    /** Lấy phiếu mượn liên kết với buổi tư vấn */
    List<BorrowSlip> findBySessionId(Long sessionId);

    /** Lấy tất cả phiếu mượn, mới nhất trước (dùng cho Admin) */
    List<BorrowSlip> findAllByOrderByCreatedAtDesc();

    /** Lấy phiếu mượn theo trạng thái */
    List<BorrowSlip> findByStatusOrderByCreatedAtDesc(BorrowSlipStatus status);

    /** Lấy phiếu mượn kèm chi tiết items (tránh N+1 query) */
    @Query("SELECT DISTINCT bs FROM BorrowSlip bs " +
           "LEFT JOIN FETCH bs.items i " +
           "LEFT JOIN FETCH i.equipment " +
           "LEFT JOIN FETCH bs.student " +
           "LEFT JOIN FETCH bs.session s " +
           "LEFT JOIN FETCH s.lecturer l " +
           "LEFT JOIN FETCH l.user " +
           "ORDER BY bs.createdAt DESC")
    List<BorrowSlip> findAllWithDetails();
}
