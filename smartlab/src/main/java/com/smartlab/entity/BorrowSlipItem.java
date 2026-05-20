package com.smartlab.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Chi tiết một dòng thiết bị trong phiếu mượn (BorrowSlip).
 * Mỗi dòng ghi nhận loại thiết bị và số lượng cần mượn.
 */
@Entity
@Table(name = "borrow_slip_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowSlipItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Số lượng mượn */
    @Column(nullable = false)
    private Integer quantity;

    /** Phiếu mượn chứa dòng này */
    @ManyToOne
    @JoinColumn(name = "borrow_slip_id", nullable = false)
    private BorrowSlip borrowSlip;

    /** Thiết bị được mượn */
    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;
}
