package com.smartlab.service;

import com.smartlab.entity.BorrowSlip;
import com.smartlab.entity.BorrowSlipItem;
import com.smartlab.entity.Equipment;
import com.smartlab.enums.BorrowSlipStatus;
import com.smartlab.repository.BorrowSlipRepository;
import com.smartlab.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service xử lý Flow 4: Duyệt xuất kho phiếu mượn (Admin / Lab Staff).
 *
 * Quy trình:
 *   1. Kiểm tra tồn kho tất cả thiết bị trong phiếu trước khi xuất
 *   2. Nếu bất kỳ thiết bị nào thiếu → từ chối toàn bộ phiếu
 *   3. Nếu đủ → trừ số lượng khả dụng + cập nhật trạng thái → RELEASED
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final BorrowSlipRepository borrowSlipRepository;
    private final EquipmentRepository equipmentRepository;

    /**
     * Lấy danh sách tất cả phiếu mượn kèm chi tiết (cho Admin).
     */
    @Transactional(readOnly = true)
    public List<BorrowSlip> getAllBorrowSlips() {
        return borrowSlipRepository.findAllWithDetails();
    }

    /**
     * Xuất kho cho phiếu mượn (Flow 4).
     *
     * @param borrowSlipId ID phiếu mượn cần xuất kho
     * @throws RuntimeException nếu thiếu tồn kho hoặc trạng thái không hợp lệ
     */
    @Transactional
    public void releaseInventory(Long borrowSlipId) {

        // --- Bước 1: Lấy phiếu mượn ---
        BorrowSlip slip = borrowSlipRepository.findById(borrowSlipId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn."));

        // Kiểm tra trạng thái: chỉ xuất kho phiếu đang PENDING
        if (slip.getStatus() != BorrowSlipStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể xuất kho phiếu đang chờ duyệt (PENDING).");
        }

        // --- Bước 2: Kiểm tra tồn kho TẤT CẢ thiết bị trước khi xuất ---
        // (Fail-fast: nếu bất kỳ thiết bị nào thiếu → từ chối toàn bộ)
        for (BorrowSlipItem item : slip.getItems()) {
            Equipment equipment = equipmentRepository.findById(item.getEquipment().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy thiết bị: " + item.getEquipment().getName()));

            if (equipment.getAvailableQuantity() < item.getQuantity()) {
                throw new RuntimeException(
                        "Thiết bị \"" + equipment.getName() + "\" không đủ tồn kho. " +
                        "Khả dụng: " + equipment.getAvailableQuantity() +
                        ", Yêu cầu: " + item.getQuantity());
            }
        }

        // --- Bước 3: Trừ số lượng tồn kho ---
        for (BorrowSlipItem item : slip.getItems()) {
            Equipment equipment = equipmentRepository.findById(item.getEquipment().getId())
                    .orElseThrow();
            equipment.setAvailableQuantity(
                    equipment.getAvailableQuantity() - item.getQuantity());
            equipmentRepository.save(equipment);
        }

        // --- Bước 4: Cập nhật trạng thái phiếu mượn → RELEASED ---
        slip.setStatus(BorrowSlipStatus.RELEASED);
        borrowSlipRepository.save(slip);
    }
}
