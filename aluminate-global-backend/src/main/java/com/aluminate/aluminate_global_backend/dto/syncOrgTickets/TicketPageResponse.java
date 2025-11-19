package com.aluminate.aluminate_global_backend.dto.syncOrgTickets;

import com.aluminate.aluminate_global_backend.model.OrgTicket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketPageResponse {
    private List<OrgTicketResponseDTO> data;  // list of tickets for this page
    private long total;            // total number of tickets across all pages
}
