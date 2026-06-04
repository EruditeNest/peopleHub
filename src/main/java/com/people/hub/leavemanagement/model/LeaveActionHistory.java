package com.people.hub.leavemanagement.model;

import com.people.hub.leavemanagement.enums.ActionRole;
import com.people.hub.leavemanagement.enums.LeaveStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class LeaveActionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long leaveApplicationId;

    private Long actionBy;

    private ActionRole actionRole;

    @Enumerated(EnumType.STRING)
    private LeaveStatus fromStatus;

    @Enumerated(EnumType.STRING)
    private LeaveStatus toStatus;

    private String remarks;

    private LocalDateTime actionDate;
}
