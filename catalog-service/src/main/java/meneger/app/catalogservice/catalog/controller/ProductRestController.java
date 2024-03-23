package meneger.app.catalogservice.catalog.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import meneger.app.catalogservice.catalog.controller.payload.UpdateProductPayload;
import meneger.app.catalogservice.catalog.entity.Product;
import meneger.app.catalogservice.catalog.service.ProductService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.Locale;
import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
@RequestMapping("catalog-api/products/{productId:\\d+}")
public class ProductRestController {
    private final ProductService productService;
    private final MessageSource messageSource;

    @ModelAttribute("product")
    public Product getProduct(@PathVariable("productId") Integer id){
        return this.productService.findProduct(id).orElseThrow(() -> new NoSuchElementException("catalog.errors.product.not_found"));
    }
    @GetMapping
    public Product findProduct(@ModelAttribute("product") Product product){
        return product;
    }
    @PatchMapping
    public ResponseEntity<?> updateProduct(@PathVariable("productId") int productId,
                                           @Valid @RequestBody UpdateProductPayload productPayload,
                                           BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()){
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        }else {
            this.productService.updateProduct(productId, productPayload.title(), productPayload.details());
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") Integer productId){
        this.productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNoSuchElementException(NoSuchElementException noSuchElementException, Locale locale){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND,
                        this.messageSource.getMessage(noSuchElementException.getMessage(), new Object[0], noSuchElementException.getMessage(), locale)));

    }


}
