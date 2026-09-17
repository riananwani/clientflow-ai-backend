package clientflow.ai.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AskQuestionResponse {
    private String question;
    private String answer;
    private List<String> sourceTitles;
    private boolean answeredFromDocuments;
}