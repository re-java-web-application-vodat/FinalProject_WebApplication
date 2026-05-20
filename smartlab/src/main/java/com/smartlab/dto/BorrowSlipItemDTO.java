package com.smartlab.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO đại diện cho một dòng thiết bị trong phiếu mượn.
 * Dùng trong form đánh giá của giảng viên (Flow 2).
 */
@Getter
@Setter
public class BorrowSlipItemDTO {

    /** ID thiết bị cần mượn */
    private Long equipmentId;

    /** Số lượng mượn */
    private Integer quantity;
}
