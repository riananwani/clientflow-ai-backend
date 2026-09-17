package clientflow.ai;

import clientflow.ai.dto.AskQuestionRequest;
import clientflow.ai.dto.AskQuestionResponse;
import clientflow.knowledge.KnowledgeDocument;
import clientflow.knowledge.KnowledgeDocumentRepository;
import clientflow.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagService {

    private final OpenAiClient openAiClient;
    private final KnowledgeDocumentRepository documentRepository;
    private final ProjectRepository projectRepository;

    public AskQuestionResponse askQuestion(Long projectId, AskQuestionRequest request) {

        // Verify project exists
        projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Extract keywords from question
        String question = request.getQuestion();
        List<String> keywords = extractKeywords(question);

        // Search documents for relevant content
        List<KnowledgeDocument> relevantDocs = new ArrayList<>();
        for (String keyword : keywords) {
            List<KnowledgeDocument> found = documentRepository
                    .searchByProjectIdAndKeyword(projectId, keyword);
            for (KnowledgeDocument doc : found) {
                if (!relevantDocs.contains(doc)) {
                    relevantDocs.add(doc);
                }
            }
        }

        // If no documents found, say so
        if (relevantDocs.isEmpty()) {
            return AskQuestionResponse.builder()
                    .question(question)
                    .answer("I don't have enough context in the project documents to answer this question.")
                    .sourceTitles(List.of())
                    .answeredFromDocuments(false)
                    .build();
        }

        // Build context from relevant documents
        String context = relevantDocs.stream()
                .map(doc -> "Document: " + doc.getTitle() + "\n" + doc.getContent())
                .collect(Collectors.joining("\n\n---\n\n"));

        List<String> sourceTitles = relevantDocs.stream()
                .map(KnowledgeDocument::getTitle)
                .collect(Collectors.toList());

        String systemPrompt = """
                You are a project assistant. Answer the user's question based ONLY on the provided project documents.
                If the documents don't contain enough information to answer, say so clearly.
                Always be concise and accurate.
                Do not make up information that is not in the documents.
                At the end of your answer, mention which document(s) you used.
                """;

        String userMessage = "Project Documents:\n\n" + context +
                "\n\n---\n\nQuestion: " + question;

        String answer = openAiClient.chat(systemPrompt, userMessage);

        return AskQuestionResponse.builder()
                .question(question)
                .answer(answer)
                .sourceTitles(sourceTitles)
                .answeredFromDocuments(true)
                .build();
    }

    private List<String> extractKeywords(String question) {
        // Remove common stop words and extract meaningful keywords
        List<String> stopWords = Arrays.asList(
                "what", "when", "where", "who", "how", "why", "is", "are",
                "the", "a", "an", "and", "or", "but", "in", "on", "at",
                "to", "for", "of", "with", "about", "does", "do", "did",
                "will", "should", "can", "could", "would", "have", "has"
        );

        return Arrays.stream(question.toLowerCase().split("\\s+"))
                .filter(word -> !stopWords.contains(word))
                .filter(word -> word.length() > 2)
                .collect(Collectors.toList());
    }
}