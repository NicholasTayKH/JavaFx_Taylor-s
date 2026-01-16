package Ai;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.langchain4j.data.document.Document;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import Ai.shared.Assistant;
import static Ai.shared.Utils.glob;
import static Ai.shared.Utils.toPath;

public class AnswerService {

    private static final Logger LOGGER = LogManager.getLogger(AnswerService.class);

    private Assistant assistant;

    private static final StreamingChatModel model = OpenAiStreamingChatModel.builder()
            .apiKey(ApiKeys.getOpenAiApiKey())
            //.modelName(GPT_3_5_TURBO)
            .modelName(GPT_4_O_MINI)
            .build();

    public void init(SearchAction action) {
        action.appendAnswer("Initiating...");
        initChat(action);
    }
    public Assistant getAssistant() {
        return assistant;
    }

    private void initChat(SearchAction action) {
        // Document path
        List<Document> documents = loadDocuments(toPath("documents/"), glob("*.txt"));
        System.out.println("internal documents = " + documents.size());

        assistant = AiServices.builder(Assistant.class)
                .streamingChatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                // Added Retrieval Augmented Generation (RAG) capability
                .contentRetriever(createContentRetriever(documents))   // it should have access to our documents
                .build();
        action.appendAnswer("Done");
        action.setFinished();
    }

    // createContentRetriever for the RAG
    private static ContentRetriever createContentRetriever(List<Document> documents) {
        // Here, we create an empty in-memory store for our documents and their embeddings.
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Added by Steve
       /* OpenAiEmbeddingModel embeddingModel = new OpenAiEmbeddingModel.OpenAiEmbeddingModelBuilder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                //.modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL)
                //.modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_LARGE)
                .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_ADA_002)
                .build();
        EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .build();
        */
        // Here, we are ingesting our documents into the store.
        // Under the hood, a lot of "magic" is happening, but we can ignore it for now.
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        // Lastly, let's create a content retriever from an embedding store.
        return EmbeddingStoreContentRetriever.from(embeddingStore);
    }
    void ask(SearchAction action) {
        LOGGER.info("Asking question '" + action.getQuestion() + "'");

        var responseHandler = new CustomStreamingResponseHandler(action);

        assistant.chat(action.getQuestion())
                .onPartialResponse(responseHandler::onNext)
                .onCompleteResponse(responseHandler::onComplete)
                .onError(responseHandler::onError)
                .start();
    }
}
