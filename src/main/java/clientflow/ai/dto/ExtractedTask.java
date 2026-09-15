package clientflow.ai.dto;

import lombok.Data;

@Data
public class ExtractedTask {
    private String title;
    private String assigneeName;
    private String priority;
    private String dueDate;
    private String description;
}