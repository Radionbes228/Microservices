package meneger.app.menegerapp.controllers;

import lombok.AllArgsConstructor;
import meneger.app.menegerapp.client.BadRequestException;
import meneger.app.menegerapp.client.ProductsRestClients;
import meneger.app.menegerapp.controllers.payload.NewProductPayload;
import meneger.app.menegerapp.entity.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("catalog/products")
public class ProductController {
    private ProductsRestClients productsRestClients;


    @GetMapping("list")
    public String getProductsList(Model model){
        model.addAttribute("products", this.productsRestClients.findAllProducts());
        return "catalog/products/list";
    }

    @GetMapping("create")
    public String getNewProductPage(){
        return "catalog/products/new_product";
    }

    @PostMapping("create")
    public String createProduct(NewProductPayload newProductPayload, Model model) {
        try {
            Product product = this.productsRestClients.createProduct(newProductPayload.title(), newProductPayload.details());
            return "redirect:/catalog/products/%s".formatted(product.id());
        } catch (
                BadRequestException exception) {
            model.addAttribute("payload", newProductPayload);
            model.addAttribute("errors", exception.getErrors());
            return "catalog/products/new_product";
        }
    }


}