package com.smartlab.service;

import com.smartlab.entity.EquipmentBorrow;
import com.smartlab.entity.MentoringSession;
import com.smartlab.repository.EquipmentBorrowRepository;
import com.smartlab.repository.MentoringSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final MentoringSessionRepository sessionRepository;
    private final EquipmentBorrowRepository borrowRepository;

    @Transactional(readOnly = true)
    public List<MentoringSession> getSessionHistory(String username) {
        return sessionRepository.findByStudent_UsernameOrderByCreatedAtDesc(username);
    }

    @Transactional(readOnly = true)
    public List<EquipmentBorrow> getBorrowHistory(String username) {
        return borrowRepository.findByStudent_UsernameOrderByCreatedAtDesc(username);
    }
}
