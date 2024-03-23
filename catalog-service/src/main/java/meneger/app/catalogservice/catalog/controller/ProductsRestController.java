package meneger.app.catalogservice.catalog.controller;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import meneger.app.catalogservice.catalog.controller.payload.NewProductPayload;
import meneger.app.catalogservice.catalog.entity.Product;
import meneger.app.catalogservice.catalog.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("catalog-api/products")
public class ProductsRestController {
    private final ProductService productService;

    @GetMapping
    public List<Product> findProducts(){
        return this.productService.findAllProducts();
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody NewProductPayload newProductPayload,
                                                 BindingResult bindingResult,
                                                 UriComponentsBuilder uriComponentsBuilder) throws BindException{
        if (bindingResult.hasErrors()){
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        }else {
            Product product = this.productService.createProduct(newProductPayload.title(), newProductPayload.details());
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/catalog-api/products/{productId}")
                            .build(Map.of("productId", product.getId())))
                    .body(product);
        }
    }
}
