package clientflow.ai;

import clientflow.ai.dto.AskQuestionRequest;
import clientflow.ai.dto.AskQuestionResponse;
import clientflow.ai.dto.ExtractTasksRequest;
import clientflow.ai.dto.ExtractTasksResponse;
import clientflow.project.Project;
import clientflow.project.ProjectRepository;
import clientflow.task.Task;
import clientflow.task.TaskRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/ai")
@RequiredArgsConstructor
public class AiController {

    private final OpenAiClient openAiClient;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final AiTaskService aiTaskService;
    private final RagService ragService;

    @GetMapping("/summarize")
    public ResponseEntity<Map<String, String>> summarizeProject(
            @PathVariable Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<Task> tasks = taskRepository.findByProjectId(projectId);

        String projectInfo = String.format(
                "Project: %s\nDescription: %s\nStatus: %s\nTotal Tasks: %d",
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                tasks.size()
        );

        String taskSummary = tasks.stream()
                .map(t -> String.format("- %s [%s] [%s]",
                        t.getTitle(), t.getStatus(), t.getPriority()))
                .reduce("", (a, b) -> a + "\n" + b);

        String systemPrompt = """
                You are a project management assistant.
                Summarize the project status clearly and concisely.
                Mention overall progress, any blockers, and key priorities.
                Keep it under 150 words.
                """;

        String userMessage = projectInfo + "\n\nTasks:\n" + taskSummary;

        String summary = openAiClient.chat(systemPrompt, userMessage);

        return ResponseEntity.ok(Map.of(
                "projectId", projectId.toString(),
                "projectName", project.getName(),
                "summary", summary
        ));
    }

    @PostMapping("/extract-tasks")
    public ResponseEntity<ExtractTasksResponse> extractTasks(
            @PathVariable Long projectId,
            @Valid @RequestBody ExtractTasksRequest request) {
        request.setProjectId(projectId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aiTaskService.extractAndCreateTasks(request));
    }

    @PostMapping("/ask")
    public ResponseEntity<AskQuestionResponse> askQuestion(
            @PathVariable Long projectId,
            @Valid @RequestBody AskQuestionRequest request) {
        return ResponseEntity.ok(ragService.askQuestion(projectId, request));
    }
}