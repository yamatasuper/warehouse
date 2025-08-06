package com.example.warehouse.service.impl;

import com.example.warehouse.controller.mapper.ProductDtoMapper;
import com.example.warehouse.controller.request.ProductCreateRequest;
import com.example.warehouse.controller.request.ProductUpdateRequest;
import com.example.warehouse.controller.response.ProductResponse;
import com.example.warehouse.currency.CurrencyProvider;
import com.example.warehouse.currency.CurrencyService;
import com.example.warehouse.currency.ExchangeRatesResponse;
import com.example.warehouse.persistence.entity.ProductEntity;
import com.example.warehouse.exception.DuplicateResourceException;
import com.example.warehouse.exception.InvalidParameterException;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.persistence.repository.ProductRepository;
import com.example.warehouse.search.ProductSpecification;
import com.example.warehouse.search.criteria.SearchCriteria;
import com.example.warehouse.search.criteria.SearchCriteriaValidator;
import com.example.warehouse.service.ProductService;
import com.example.warehouse.service.ProductServiceMapper;
import com.example.warehouse.service.model.Product;
import com.example.warehouse.service.request.CreateProductCommand;
import com.example.warehouse.service.request.UpdateProductCommand;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;
/**
 * Реализация сервиса для работы с продуктами.
 * Предоставляет CRUD-операции и бизнес-логику для управления продуктами.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductServiceMapper serviceMapper;
    private final ProductDtoMapper dtoMapper;
    private final SearchCriteriaValidator searchCriteriaValidator;

    private final CurrencyService currencyService;
    private final CurrencyProvider currencyProvider;

    @Override
    @Transactional
    public UUID create(ProductCreateRequest request) {
        ProductEntity entity = dtoMapper.toEntity(request);
        ProductEntity saved = productRepository.save(entity);
        return saved.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(UUID id) {
        return productRepository.findById(id)
                .map(serviceMapper::toDomain)
                .map(this::convertToResponseWithCurrency)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Override
    @Transactional
    public ProductResponse update(UUID id, ProductUpdateRequest request) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        dtoMapper.updateEntity(entity, request);
        ProductEntity updated = productRepository.save(entity);

        return dtoMapper.toResponse(serviceMapper.toDomain(updated));
    }


    @Override
    @Transactional
    public void delete(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(serviceMapper::toDomain)
                .map(this::convertToResponseWithCurrency)
                .toList();
    }

    @Override
    public Page<ProductResponse> searchProducts(List<SearchCriteria> criteria, int page, int size) {
        if (criteria != null && !criteria.isEmpty()) {
            searchCriteriaValidator.validate(criteria, ProductEntity.class);
        }

        Specification<ProductEntity> spec = criteria == null || criteria.isEmpty()
                ? null
                : createSpecification(criteria.get(0));

        for (int i = 1; i < criteria.size(); i++) {
            spec = spec.and(createSpecification(criteria.get(i)));
        }

        return productRepository.findAll(spec, PageRequest.of(page, size))
                .map(serviceMapper::toDomain)
                .map(this::convertToResponseWithCurrency);
    }

    private Specification<ProductEntity> createSpecification(SearchCriteria criteria) {
        return new ProductSpecification(criteria);
    }

    private ProductResponse convertToResponseWithCurrency(Product product) {
        String targetCurrency = currencyProvider.getCurrency();
        BigDecimal price = product.price(); // для record

        if (!"RUB".equals(targetCurrency)) {
            ExchangeRatesResponse rates = currencyService.getExchangeRates();
            price = currencyService.convertPrice(
                    product.price(),
                    "RUB",
                    targetCurrency,
                    rates
            );
        }

        return ProductResponse.builder()
                .id(product.id())
                .name(product.name())
                .article(product.article())
                .description(product.description())
                .category(product.category())
                .price(price)
                .quantity(product.quantity())
                .lastQuantityChange(product.lastQuantityChange())
                .createdAt(product.createdAt())
                .currency(targetCurrency)
                .build();
    }
}
