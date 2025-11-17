package com.aluminate.aluminate_global_backend.repository;

import com.aluminate.aluminate_global_backend.dto.syncOrgTickets.OrgTicketProjection;
import com.aluminate.aluminate_global_backend.dto.syncOrgTickets.OrgTicketResponseDTO;
import com.aluminate.aluminate_global_backend.model.OrgTicket;
import com.aluminate.aluminate_global_backend.model.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;


@Repository
public interface OrgTicketRepository extends JpaRepository<OrgTicket, Long> {

    @Query("""
SELECT 
    t.id AS id,
    t.amount AS amount,
    t.status AS status,
    o.id AS organizationId,
    o.organizationName AS organizationName,
    t.createdAt AS createdAt
FROM OrgTicket t
JOIN t.organization o
WHERE LOWER(o.organizationName) LIKE LOWER(CONCAT('%', :search, '%'))
  AND (:status IS NULL OR t.status = :status)
  AND t.createdAt >= :from
  AND t.createdAt <= :to
""")
    Page<OrgTicketProjection> findTickets(
            @Param("search") String search,
            @Param("status") TransactionStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );




}

