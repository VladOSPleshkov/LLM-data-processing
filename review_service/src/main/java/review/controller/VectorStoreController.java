package review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.CrudController;
import com.shared.dto.review.CreateVectorRequest;
import com.shared.dto.review.UpdateVectorRequest;
import com.shared.dto.review.VectorStoreDto;
import com.shared.service.IVectorStoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.shared.dto.DeleteStatus;

import java.util.UUID;

@Component("vectorStoreController")
public class VectorStoreController extends AbstractReviewController
        implements CrudController<CreateVectorRequest, VectorStoreDto, UpdateVectorRequest, UUID> {

    private final IVectorStoreService vectorStoreService;

    public VectorStoreController(IVectorStoreService vectorStoreService, ObjectMapper objectMapper) {
        super(objectMapper, "vectorStoreController");
        this.vectorStoreService = vectorStoreService;
    }

    @Override
    public String processCreate(CreateVectorRequest request) { return toJson(vectorStoreService.createVector(request)); }

    @Override
    public String processGetById(UUID id) { return toJson(vectorStoreService.getVectorById(id)); }

    @Override
    public String processGetAll() { return toJson(vectorStoreService.getAllVectors()); }

    @Override
    public String processUpdate(UUID id, UpdateVectorRequest request) { return toJson(vectorStoreService.updateVector(id, request)); }

    @Override
    public String processDelete(UUID id) {
        boolean deleted = vectorStoreService.deleteVector(id);
        return toJson(new DeleteStatus(deleted, "Вектор с ID " + id + " обработан"));
    }

    @Override
    public String execute() { return "vectorStoreController активен"; }
}