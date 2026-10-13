package review.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.dto.review.BrandDto;
import com.shared.dto.review.CreateBrandRequest;
import com.shared.dto.review.UpdateBrandRequest;
import com.shared.service.IBrandsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.shared.dto.DeleteStatus;
import org.springframework.web.bind.annotation.*;
import review.service.BrandsService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/brands")
public class BrandsController {

    private final IBrandsService brandService;

    public BrandsController(IBrandsService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getBrandById(@PathVariable UUID id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @PostMapping
    public ResponseEntity<BrandDto> createBrand(@RequestBody CreateBrandRequest brandRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.createBrand(brandRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable UUID id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}