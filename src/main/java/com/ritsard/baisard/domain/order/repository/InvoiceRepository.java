package com.ritsard.baisard.domain.order.repository;

import com.ritsard.baisard.domain.member.entity.Member;
import com.ritsard.baisard.domain.order.entity.Invoice;
import com.ritsard.baisard.domain.order.entity.SaleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    /**
     * Get the last (maximum) invoice number for a specific terminal and mode.
     * This ensures each terminal has its own independent invoice sequence,
     * just like standalone POS machines.
     * <p>
     * Example:
     * - Terminal A, Live Mode: 1, 2, 3, 4...
     * - Terminal A, Train Mode: 9000001, 9000002...
     * - Terminal B, Live Mode: 1, 2, 3, 4... (independent from Terminal A)
     * - Terminal B, Train Mode: 9000001, 9000002... (independent from Terminal A)
     *
     * @param terminalId  The UUID of the POS terminal
     * @param isTrainMode Whether to get train mode or live mode invoices
     * @return The highest invoice number, or null if no invoices exist
     */
    @Query("""
                SELECT MAX(i.invoiceNumber)
                FROM Invoice i
                WHERE i.terminal.uuidPosTerminal = :terminalId
                AND i.isTrainMode = :isTrainMode
                AND i.isDeleted = false
            """)
    Long findLastInvoiceNumberByTerminal(
            @Param("terminalId") UUID terminalId,
            @Param("isTrainMode") boolean isTrainMode
    );


//    /**
//     * Optional: Get count of invoices for a terminal (useful for Z-reading)
//     */
//    @Query("""
//                SELECT COUNT(i)
//                FROM Invoice i
//                WHERE i.terminal.uuidPosTerminal = :terminalId
//                AND i.isTrainMode = :isTrainMode
//                AND i.isDeleted = false
//                AND i.status = 'PAID'
//            """)
//    Long countPaidInvoicesByTerminal(
//            @Param("terminalId") UUID terminalId,
//            @Param("isTrainMode") boolean isTrainMode
//    );
}
