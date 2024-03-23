package meneger.app.menegerapp.client;

import meneger.app.menegerapp.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductsRestClients {

    List<Product> findAllProducts();
    Product createProduct(String title, String details);

    Optional<Product> findProduct(int productId);
    void updateProduct(int productId, String title, String details);
    void deleteProduct(int productId);

}
