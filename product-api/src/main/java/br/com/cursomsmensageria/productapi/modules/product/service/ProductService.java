package br.com.cursomsmensageria.productapi.modules.product.service;

import br.com.cursomsmensageria.productapi.config.exception.SuccessResponse;
import br.com.cursomsmensageria.productapi.config.exception.ValidationException;
import br.com.cursomsmensageria.productapi.modules.category.service.CategoryService;
import br.com.cursomsmensageria.productapi.modules.product.dto.ProductRequest;
import br.com.cursomsmensageria.productapi.modules.product.dto.ProductResponse;
import br.com.cursomsmensageria.productapi.modules.product.model.Product;
import br.com.cursomsmensageria.productapi.modules.product.repository.ProductRepository;
import br.com.cursomsmensageria.productapi.modules.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class ProductService {

    private static final Integer ZERO = 0;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private CategoryService categoryService;


    public Product findById(Integer id){
        validateInformedId(id);
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There's no product for the given ID."));
    }

    public ProductResponse findByIdResponse(Integer id){
        return ProductResponse.of(findById(id));
    }

    public List<ProductResponse> findAll(){
        return productRepository
                .findAll()
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByName(String name){
        if(isEmpty(name)){
            throw new ValidationException("The product name must be informed.");
        }
        return productRepository
                .findByNameIgnoreCaseContaining(name)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findBySupplierId(Integer supplierId){
        if(isEmpty(supplierId)){
            throw new ValidationException("The products supplier ID must be informed.");
        }
        return productRepository
                .findBySupplierId(supplierId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByCategoryId(Integer categoryID){
        if(isEmpty(categoryID)){
            throw new ValidationException("The products category ID must be informed.");
        }
        return productRepository
                .findBySupplierId(categoryID)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }


    public ProductResponse save(ProductRequest request) {
        validateProductDataInformed(request);
        validateCategoryAndSuppierIdInformed(request);
        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());
        var product = productRepository.save(Product.of(request, supplier, category));
        return ProductResponse.of(product);
    }


    public ProductResponse update(ProductRequest request, Integer id) {
        validateProductDataInformed(request);
        validateCategoryAndSuppierIdInformed(request);
        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());
        var product = Product.of(request, supplier, category);
        product.setId(id);
        productRepository.save(product);
        return ProductResponse.of(product);
    }


    public SuccessResponse delete(Integer id){
        validateInformedId(id);
        if (productRepository.findById(id) == null){
            throw new ValidationException("Id don't exists.");
        }
        productRepository.deleteById(id);
        return SuccessResponse.create("The product was deleted.");
    }

    private void validateInformedId(Integer id){
        if (isEmpty(id)) {
            throw new ValidationException("Id was not informed.");
        }
    }

    public Boolean existsByCategoryId(Integer categoryId){
        return productRepository.existsByCategoryId(categoryId);

    }

    public Boolean existsBySupplierId(Integer supplierId){
        return productRepository.existsByCategoryId(supplierId);

    }


    private void validateProductDataInformed(ProductRequest request){
        if (isEmpty(request.getName())){
            throw new ValidationException("The product's name was not informed.");
        }

        if (isEmpty(request.getQuantityAvailable())){
            throw new ValidationException("The product's quantity was not informed.");

        }

        if (request.getQuantityAvailable() <= ZERO) {
            throw new ValidationException("The quantity should not be less or equals to zero.");
        }
    }

    private void validateCategoryAndSuppierIdInformed(ProductRequest request){
        if (isEmpty(request.getCategoryId())){
            throw new ValidationException("The category ID was not informed.");
        }

        if (isEmpty(request.getSupplierId())){
            throw new ValidationException("The supplier ID was not informed.");
        }
    }

}
