package clientflow.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AskQuestionRequest {

    @NotBlank(message = "Question is required")
    private String question;
}