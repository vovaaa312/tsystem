package com.tsystem.repository;

import com.tsystem.model.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistory, UUID> {

    List<TicketHistory> findByTicket_IdOrderByChangedAtAsc(UUID ticketId);
}
