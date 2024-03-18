package meneger.app.catalogservice.catalog.service;



import lombok.AllArgsConstructor;
import meneger.app.catalogservice.catalog.entity.Product;
import meneger.app.catalogservice.catalog.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DefaultProductServiceImpl implements ProductService {
    private ProductRepository productRepository;

    @Override
    public List<Product> findAllProducts() {
        return this.productRepository.findAll();
    }

    @Override
    public Product createProduct(String title, String details) {
       return this.productRepository.save(new Product(null, title, details));
    }

    @Override
    public Optional<Product> findProduct(int productId) {
        return this.productRepository.findById(productId);
    }

    @Override
    public void updateProduct(Integer id, String title, String details) {
        this.productRepository.findById(id).ifPresentOrElse(product -> {
            product.setTitle(title);
            product.setDetails(details);
        }, () -> {
            throw new NoSuchElementException();
        });
    }

    @Override
    public void deleteProduct(Integer id) {
        this.productRepository.deleteById(id);
    }
}
