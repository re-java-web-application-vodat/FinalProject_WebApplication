package com.smartlab.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO để giảng viên gửi đánh giá sau buổi tư vấn (Flow 2).
 * Bao gồm phần đánh giá năng lực và danh sách thiết bị cho sinh viên mượn.
 */
@Getter
@Setter
public class EvaluationDTO {

    // ===== Phần đánh giá năng lực =====

    /** Nội dung đánh giá chính */
    private String assessmentContent;

    /** Điểm năng lực (0.0 – 10.0) */
    private Double score;

    /** Điểm mạnh */
    private String strengths;

    /** Điểm cần cải thiện */
    private String weaknesses;

    /** Đề xuất / khuyến nghị */
    private String recommendations;

    // ===== Phần phiếu mượn thiết bị =====

    /** Ghi chú cho phiếu mượn */
    private String note;

    /** Danh sách thiết bị cần mượn (có thể rỗng nếu không cần mượn) */
    private List<BorrowSlipItemDTO> borrowItems = new ArrayList<>();
}
