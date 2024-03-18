package meneger.app.menegerapp.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import meneger.app.menegerapp.controllers.payload.NewProductPayload;
import meneger.app.menegerapp.entity.Product;
import meneger.app.menegerapp.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("catalog/products")
public class ProductController {
    private ProductService productService;


    @GetMapping("list")
    public String getProductsList(Model model){
        model.addAttribute("products", this.productService.findAllProducts());
        return "catalog/products/list";
    }

    @GetMapping("create")
    public String getNewProductPage(){
        return "catalog/products/new_product";
    }

    @PostMapping("create")
    public String createProduct(@Valid NewProductPayload newProductPayload, BindingResult bindingResult, Model model){
       if(bindingResult.hasErrors()){
           model.addAttribute("payload", newProductPayload);
           model.addAttribute("errors", bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList());
           return "catalog/products/new_product";
       } else {
           Product product = this.productService.createProduct(newProductPayload.title(), newProductPayload.details());
           return "redirect:/catalog/products/%s".formatted(product.getId());
       }
    }


}