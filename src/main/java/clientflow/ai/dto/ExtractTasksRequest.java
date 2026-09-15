package clientflow.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExtractTasksRequest {

    private Long projectId;

    @NotBlank(message = "Meeting notes are required")
    private String meetingNotes;
}