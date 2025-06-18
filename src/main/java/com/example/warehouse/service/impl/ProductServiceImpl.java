package com.example.warehouse.service.impl;

import com.example.warehouse.controller.mapper.ProductDtoMapper;
import com.example.warehouse.controller.request.ProductCreateRequest;
import com.example.warehouse.controller.request.ProductUpdateRequest;
import com.example.warehouse.controller.response.ProductResponse;
import com.example.warehouse.entity.ProductEntity;
import com.example.warehouse.exception.DeleteResourceException;
import com.example.warehouse.exception.DuplicateResourceException;
import com.example.warehouse.exception.InvalidParameterException;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.service.ProductService;
import com.example.warehouse.service.ProductServiceMapper;
import com.example.warehouse.service.model.Product;
import com.example.warehouse.service.request.CreateProductCommand;
import com.example.warehouse.service.request.UpdateProductCommand;

import org.springframework.stereotype.Service;

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
    private final ProductServiceMapper productServiceMapper;
    private final ProductDtoMapper productDtoMapper;

    /**
     * Создает новый продукт на основе переданных данных.
     *
     * @param request DTO с данными для создания продукта
     * @return DTO созданного продукта
     * @throws InvalidParameterException если запрос или обязательные параметры невалидны
     * @throws DuplicateResourceException если продукт с таким артикулом уже существует
     */
    @Override
    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        validateProductCreateRequest(request);
        validateArticleUniqueness(request.getArticle());

        CreateProductCommand command = productDtoMapper.toCommand(request);
        ProductEntity entity = productServiceMapper.toEntity(command);
        ProductEntity saved = productRepository.save(entity);

        Product product = productServiceMapper.toDomain(saved);
        return productDtoMapper.toResponse(product);
    }

    /**
     * Возвращает продукт по его идентификатору.
     *
     * @param id UUID идентификатор продукта
     * @return DTO найденного продукта
     * @throws InvalidParameterException если идентификатор равен null
     * @throws ResourceNotFoundException если продукт с указанным ID не найден
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(UUID id) {
        validateIdNotNull(id);
        return productRepository.findById(id)
                .map(productServiceMapper::toDomain)
                .map(productDtoMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    /**
     * Обновляет существующий продукт.
     *
     * @param id UUID идентификатор обновляемого продукта
     * @param request DTO с данными для обновления
     * @return DTO обновленного продукта
     * @throws InvalidParameterException если запрос или параметры невалидны
     * @throws ResourceNotFoundException если продукт с указанным ID не найден
     * @throws DuplicateResourceException если новый артикул уже существует
     */
    @Override
    @Transactional
    public ProductResponse update(UUID id, ProductUpdateRequest request) {
        validateIdNotNull(id);
        validateProductUpdateRequest(request);

        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        if (request.getArticle() != null && !request.getArticle().equals(entity.getArticle())) {
            validateArticleUniqueness(request.getArticle());
        }

        UpdateProductCommand command = productDtoMapper.toCommand(request);
        productServiceMapper.updateEntity(command, entity);
        ProductEntity updated = productRepository.save(entity);

        Product product = productServiceMapper.toDomain(updated);
        return productDtoMapper.toResponse(product);
    }

    /**
     * Удаляет продукт по его идентификатору.
     *
     * @param id UUID идентификатор удаляемого продукта
     * @throws InvalidParameterException если идентификатор равен null
     * @throws ResourceNotFoundException если продукт с указанным ID не найден
     */
    @Override
    @Transactional
    public void delete(UUID id) {
        validateIdNotNull(id);
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Возвращает список всех продуктов.
     *
     * @return список DTO всех продуктов
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(productServiceMapper::toDomain)
                .map(productDtoMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Проверяет уникальность артикула продукта.
     *
     * @param article артикул для проверки
     * @throws InvalidParameterException если артикул равен null или пустой
     * @throws DuplicateResourceException если продукт с таким артикулом уже существует
     */
    private void validateArticleUniqueness(String article) {
        if (article == null || article.isBlank()) {
            throw new InvalidParameterException("Article cannot be null or empty");
        }
        if (productRepository.existsByArticle(article)) {
            throw new DuplicateResourceException("Product with article " + article + " already exists");
        }
    }

    /**
     * Проверяет что идентификатор не равен null.
     *
     * @param id идентификатор для проверки
     * @throws InvalidParameterException если идентификатор равен null
     */
    private void validateIdNotNull(UUID id) {
        if (id == null) {
            throw new InvalidParameterException("ID cannot be null");
        }
    }

    /**
     * Валидирует запрос на создание продукта.
     *
     * @param request запрос для валидации
     * @throws InvalidParameterException если запрос или обязательные поля невалидны
     */
    private void validateProductCreateRequest(ProductCreateRequest request) {
        if (request == null) {
            throw new InvalidParameterException("Request cannot be null");
        }
        if (request.getArticle() == null || request.getArticle().isBlank()) {
            throw new InvalidParameterException("Article cannot be null or empty");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidParameterException("Name cannot be null or empty");
        }
    }

    /**
     * Валидирует запрос на обновление продукта.
     *
     * @param request запрос для валидации
     * @throws InvalidParameterException если запрос или поля невалидны
     */
    private void validateProductUpdateRequest(ProductUpdateRequest request) {
        if (request == null) {
            throw new InvalidParameterException("Request cannot be null");
        }
        if (request.getArticle() != null && request.getArticle().isBlank()) {
            throw new InvalidParameterException("Article cannot be empty");
        }
        if (request.getName() != null && request.getName().isBlank()) {
            throw new InvalidParameterException("Name cannot be empty");
        }
    }
}