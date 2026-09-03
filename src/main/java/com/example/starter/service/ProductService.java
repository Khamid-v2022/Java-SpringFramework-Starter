package com.example.starter.service;

import com.example.starter.dao.ProductDao;
import com.example.starter.domain.Product;
import com.example.starter.dto.ProductForm;
import com.example.starter.dto.ProductResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductDao productDao;
    private final ModelMapper modelMapper;

    public ProductService(ProductDao productDao, ModelMapper modelMapper) {
        this.productDao = productDao;
        this.modelMapper = modelMapper;
    }

    public List<Product> findAll() {
        return productDao.findAll();
    }

    public List<Product> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return findAll();
        }
        return productDao.findByNameContaining(keyword.trim());
    }

    public Product findById(Long id) {
        return productDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    public ProductResponse toResponse(Product product) {
        return modelMapper.map(product, ProductResponse.class);
    }

    public List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public Product create(ProductForm form) {
        Product product = modelMapper.map(form, Product.class);
        product.setId(null);
        return productDao.save(product);
    }

    @Transactional
    public Product update(Long id, ProductForm form) {
        Product product = findById(id);
        product.setName(form.getName());
        product.setDescription(form.getDescription());
        product.setPrice(form.getPrice());
        product.setQuantity(form.getQuantity());
        return product;
    }

    @Transactional
    public void delete(Long id) {
        Product product = findById(id);
        productDao.delete(product);
    }

    public long count() {
        return productDao.count();
    }
}
