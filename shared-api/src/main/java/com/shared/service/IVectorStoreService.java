package com.shared.service;

import com.shared.dto.review.CreateVectorRequest;
import com.shared.dto.review.UpdateVectorRequest;
import com.shared.dto.review.VectorStoreDto;
import java.util.List;
import java.util.UUID;

public interface IVectorStoreService {
    VectorStoreDto createVector(CreateVectorRequest request);
    VectorStoreDto getVectorById(UUID id);
    List<VectorStoreDto> getAllVectors();
    VectorStoreDto updateVector(UUID id, UpdateVectorRequest request);
    boolean deleteVector(UUID id);
}