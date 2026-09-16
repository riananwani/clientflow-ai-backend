package clientflow.knowledge;

import clientflow.knowledge.dto.DocumentResponse;
import clientflow.knowledge.dto.UploadDocumentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/documents")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @PostMapping
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable Long projectId,
            @Valid @RequestBody UploadDocumentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(knowledgeService.uploadDocument(projectId, request));
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getDocuments(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(knowledgeService.getDocumentsByProject(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(
            @PathVariable Long projectId,
            @PathVariable Long id) {
        return ResponseEntity.ok(knowledgeService.getDocumentById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long projectId,
            @PathVariable Long id) {
        knowledgeService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}