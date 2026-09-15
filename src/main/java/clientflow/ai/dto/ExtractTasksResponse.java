package clientflow.ai.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ExtractTasksResponse {
    private Long projectId;
    private String projectName;
    private int tasksCreated;
    private List<ExtractedTask> extractedTasks;
}