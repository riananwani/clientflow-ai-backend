package clientflow.project.dto;

import clientflow.project.ProjectStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private ProjectStatus status;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
}