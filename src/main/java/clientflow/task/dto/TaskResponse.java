package clientflow.task.dto;

import clientflow.task.TaskPriority;
import clientflow.task.TaskStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Long projectId;
    private String projectName;
    private Long assigneeId;
    private String assigneeName;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
}