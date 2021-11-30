package br.com.cursomsmensageria.productapi.modules.supplier.service;

import br.com.cursomsmensageria.productapi.config.exception.SuccessResponse;
import br.com.cursomsmensageria.productapi.config.exception.ValidationException;
import br.com.cursomsmensageria.productapi.modules.product.service.ProductService;
import br.com.cursomsmensageria.productapi.modules.supplier.dto.SupplierRequest;
import br.com.cursomsmensageria.productapi.modules.supplier.dto.SupplierResponse;
import br.com.cursomsmensageria.productapi.modules.supplier.model.Supplier;
import br.com.cursomsmensageria.productapi.modules.supplier.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductService productService;



    public Supplier findById(Integer id){
        validateInformedId(id);
        return supplierRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There's no supplier for the given ID."));
    }

    public List<SupplierResponse> findAll(){
        return supplierRepository
                .findAll()
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public List<SupplierResponse> findByName(String name){
        if(isEmpty(name)){
            throw new ValidationException("The supplier name must be informed.");
        }
        return supplierRepository
                .findByNameIgnoreCaseContaining(name)
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public SupplierResponse findByIdResponse(Integer id){
        if (isEmpty(id)){
            throw new ValidationException("The category ID was not informed.");
        }
        return SupplierResponse.of(findById(id));
    }

    public SupplierResponse save(SupplierRequest request) {
        validateSupplierNameInformed(request);
        var supplier = supplierRepository.save(Supplier.of(request));
        return SupplierResponse.of(supplier);
    }

    public SupplierResponse update(SupplierRequest request, Integer id) {
        validateSupplierNameInformed(request);
        var supplier= Supplier.of(request);
        validateInformedId(id);
        supplier.setId(id);
        supplierRepository.save(supplier);
        return SupplierResponse.of(supplier);
    }

    public SuccessResponse delete(Integer id){
        validateInformedId(id);
        if (productService.existsBySupplierId(id)){
            throw new ValidationException("You can not delete this supplier because it's already defined by a product.");
        }
        supplierRepository.deleteById(id);
        return SuccessResponse.create("The supplier was deleted.");
    }

    private void validateInformedId(Integer id){
        if (isEmpty(id)){
            throw new ValidationException("Id was not informed.");
        }
    }

    private void validateSupplierNameInformed(SupplierRequest request){
        if (isEmpty(request.getName())){
            throw new ValidationException("The supplier's name was not informed.");
        }
    }
}
