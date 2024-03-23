package meneger.app.menegerapp.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import meneger.app.menegerapp.client.BadRequestException;
import meneger.app.menegerapp.client.ProductsRestClients;
import meneger.app.menegerapp.controllers.payload.UpdateProductPayload;
import meneger.app.menegerapp.entity.Product;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.NoSuchElementException;


@Controller
@RequiredArgsConstructor
@RequestMapping("catalog/products/{productId:\\d+}")
public class ProductsController {

    private final ProductsRestClients productsRestClients;
    private final MessageSource messageSource;

    @ModelAttribute("product")
    public Product product(@PathVariable("productId") int productId){
        return this.productsRestClients.findProduct(productId).orElseThrow(() -> new NoSuchElementException("catalog.errors.product.not_found"));
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
    public String updateProduct(@ModelAttribute(value = "product", binding = false) Product product, UpdateProductPayload updateProductPayload, Model model) {
        try {
            this.productsRestClients.updateProduct(product.id(), updateProductPayload.title(), updateProductPayload.details());
            return "redirect:/catalog/products/%s".formatted(product.id());
        } catch (BadRequestException exception) {
            model.addAttribute("payload", updateProductPayload);
            model.addAttribute("errors", exception.getErrors());
            return "catalog/products/edit";
        }
    }
    @PostMapping("delete")
    public String deleteProduct(@ModelAttribute("product") Product product){
        this.productsRestClients.deleteProduct(product.id());
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
