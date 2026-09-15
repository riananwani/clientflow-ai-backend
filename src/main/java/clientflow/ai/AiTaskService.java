package clientflow.ai;

import clientflow.ai.dto.ExtractTasksRequest;
import clientflow.ai.dto.ExtractTasksResponse;
import clientflow.ai.dto.ExtractedTask;
import clientflow.project.Project;
import clientflow.project.ProjectRepository;
import clientflow.task.Task;
import clientflow.task.TaskPriority;
import clientflow.task.TaskRepository;
import clientflow.task.TaskStatus;
import clientflow.user.User;
import clientflow.user.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiTaskService {

    private final OpenAiClient openAiClient;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExtractTasksResponse extractAndCreateTasks(ExtractTasksRequest request) {

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        String systemPrompt = """
                You are a project management assistant.
                Extract action items from the meeting notes and return them as a JSON array.
                Each item must have these fields:
                - title: short task title
                - assigneeName: person responsible (first name only, or "Unassigned")
                - priority: one of LOW, MEDIUM, HIGH, CRITICAL
                - dueDate: in format yyyy-MM-dd HH:mm:ss (or null if not mentioned)
                - description: brief description of the task
                
                Return ONLY a valid JSON array, no explanation, no markdown, no backticks.
                Example: [{"title":"Fix login bug","assigneeName":"John","priority":"HIGH","dueDate":"2026-10-01 00:00:00","description":"Fix the login page bug"}]
                """;

        String aiResponse = openAiClient.chat(systemPrompt, request.getMeetingNotes());

        List<ExtractedTask> extractedTasks;
        try {
            // Clean response in case model adds markdown
            String cleaned = aiResponse.trim()
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();
            extractedTasks = objectMapper.readValue(cleaned,
                    new TypeReference<List<ExtractedTask>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage());
        }

        // Save each extracted task to database
        for (ExtractedTask extracted : extractedTasks) {
            TaskPriority priority;
            try {
                priority = TaskPriority.valueOf(extracted.getPriority().toUpperCase());
            } catch (Exception e) {
                priority = TaskPriority.MEDIUM;
            }

            LocalDateTime dueDate = null;
            if (extracted.getDueDate() != null && !extracted.getDueDate().equals("null")) {
                try {
                    dueDate = LocalDateTime.parse(extracted.getDueDate(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } catch (DateTimeParseException e) {
                    dueDate = null;
                }
            }

            // Try to find assignee by first name
            User assignee = null;
            if (extracted.getAssigneeName() != null &&
                    !extracted.getAssigneeName().equalsIgnoreCase("Unassigned")) {
                List<User> allUsers = userRepository.findAll();
                Optional<User> found = allUsers.stream()
                        .filter(u -> u.getName().toLowerCase()
                                .contains(extracted.getAssigneeName().toLowerCase()))
                        .findFirst();
                assignee = found.orElse(null);
            }

            Task task = Task.builder()
                    .title(extracted.getTitle())
                    .description(extracted.getDescription())
                    .status(TaskStatus.TODO)
                    .priority(priority)
                    .project(project)
                    .assignee(assignee)
                    .dueDate(dueDate)
                    .build();

            taskRepository.save(task);
        }

        return ExtractTasksResponse.builder()
                .projectId(project.getId())
                .projectName(project.getName())
                .tasksCreated(extractedTasks.size())
                .extractedTasks(extractedTasks)
                .build();
    }
}