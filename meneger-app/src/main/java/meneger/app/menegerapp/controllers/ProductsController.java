package meneger.app.menegerapp.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import meneger.app.menegerapp.controllers.payload.UpdateProductPayload;
import meneger.app.menegerapp.entity.Product;
import meneger.app.menegerapp.service.ProductService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.NoSuchElementException;


@Controller
@AllArgsConstructor
@RequestMapping("catalog/products/{productId:\\d+}")
public class ProductsController {

    private final ProductService productService;
    private final MessageSource messageSource;

    @ModelAttribute("product")
    public Product product(@PathVariable("productId") int productId){
        return this.productService.findProduct(productId).orElseThrow(() -> new NoSuchElementException("catalog.errors.product.not_found"));
    }

    @GetMapping
    public String getProduct(){
        return "catalog/products/product";
    }

    @GetMapping("edit")
    public String getProductEditPage(){
        return "catalog/products/edit";
    }

    @PostMapping("edit")
    public String updateProduct(@ModelAttribute(value = "product", binding = false) Product product, @Valid UpdateProductPayload updateProductPayload, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()) {
            model.addAttribute("payload", updateProductPayload);
            model.addAttribute("errors", bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList());
            return "catalog/products/edit";
        }else {
            this.productService.updateProduct(product.getId(), updateProductPayload.title(), updateProductPayload.details());
            return "redirect:/catalog/products/%s".formatted(product.getId());
        }
    }
    @PostMapping("delete")
    public String deleteProduct(@ModelAttribute("product") Product product){
        this.productService.deleteProduct(product.getId());
        return "redirect:/catalog/products/list";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handlerNoSuchElementException(Model model, NoSuchElementException noSuchElementException, HttpServletResponse response, Locale locale){
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        model.addAttribute("error", this.messageSource.getMessage(noSuchElementException.getMessage(), new Object[0],
                noSuchElementException.getMessage(), locale));
        return "errors/404";
    }
}
