package clientflow.knowledge.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DocumentResponse {
    private Long id;
    private String title;
    private String content;
    private Long projectId;
    private String projectName;
    private Long uploadedById;
    private String uploadedByName;
    private LocalDateTime createdAt;
}