package com.smartlab.enums;

/**
 * Trạng thái phiếu mượn thiết bị (do giảng viên tạo).
 * PENDING  – Chờ Admin duyệt xuất kho
 * RELEASED – Đã xuất kho cho sinh viên
 * RETURNED – Sinh viên đã trả thiết bị
 */
public enum BorrowSlipStatus {
    PENDING,
    RELEASED,
    RETURNED
}
