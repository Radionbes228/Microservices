package meneger.app.catalogservice.catalog.repository;


import lombok.extern.slf4j.Slf4j;
import meneger.app.catalogservice.catalog.entity.Product;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.IntStream;

@Component
@Slf4j
public class InMemoryProductRepositoryImpl implements ProductRepository {
    private final List<Product> products = Collections.synchronizedList(new LinkedList<>());

    public InMemoryProductRepositoryImpl(){
        IntStream.range(1,4)
                .forEach(i -> this.products.add(new Product(i, String.format("Товар N%s", i), String.format("Описание Товара N%s", i))));
        for (Product product: products){
            log.info(String.valueOf(product.getTitle()));
        }
    }

    @Override
    public List<Product> findAll() {
        return Collections.unmodifiableList(products);
    }

    @Override
    public Product save(Product product) {
        product.setId(this.products.stream()
                .max(Comparator.comparingInt(Product::getId))
                .map(Product::getId)
                .orElse(0) +1
        );
        this.products.add(product);
        return product;
    }

    @Override
    public Optional<Product> findById(Integer productId) {
        return this.products.stream().filter(product -> Objects.equals(productId, product.getId())).findFirst();
    }

    @Override
    public void deleteById(Integer id) {
        this.products.removeIf(product -> Objects.equals(id, product.getId()));
    }
}
