package meneger.app.menegerapp.client;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import meneger.app.menegerapp.controllers.payload.NewProductPayload;
import meneger.app.menegerapp.controllers.payload.UpdateProductPayload;
import meneger.app.menegerapp.entity.Product;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
public class RestClientProductServiceClient implements ProductsRestClients{
    private static final ParameterizedTypeReference<List<Product>> PRODUCTS_TYPE_REFERENCE = new ParameterizedTypeReference<>(){};
    private final RestClient restClient;
    @Override
    public List<Product> findAllProducts() {
        return restClient
                .get()
                .uri("/catalog-api/products")
                .retrieve()
                .body(PRODUCTS_TYPE_REFERENCE);
    }

    @Override
    public Product createProduct(String title, String details) {
        try {
            return this.restClient
                    .post()
                    .uri("/catalog-api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new NewProductPayload(title, details))
                    .retrieve()
                    .body(Product.class);
        }catch (HttpClientErrorException.BadRequest badRequest){
            ProblemDetail responseBodyAs = badRequest.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) responseBodyAs.getProperties().get("errors"));
        }

    }

    @Override
    public Optional<Product> findProduct(int productId) {
        try {
            return Optional.ofNullable(this.restClient.get()
                    .uri("/catalog-api/products/{productId}", productId)
                    .retrieve()
                    .body(Product.class));
        }catch (HttpClientErrorException.NotFound notFound){
            return Optional.empty();
        }
    }

    @Override
    public void updateProduct(int productId, String title, String details) {
        try {
            this.restClient
                    .patch()
                    .uri("/catalog-api/products/{productId}", productId )
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateProductPayload(title, details))
                    .retrieve()
                    .toBodilessEntity();
        }catch (HttpClientErrorException.BadRequest badRequest){
            ProblemDetail responseBodyAs = badRequest.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) responseBodyAs.getProperties().get("errors"));
        }
    }

    @Override
    public void deleteProduct(int productId) {
        try {
            Optional.of(this.restClient.delete()
                    .uri("/catalog-api/products/{productId}", productId)
                    .retrieve()
                    .toBodilessEntity());
        }catch (HttpClientErrorException.NotFound notFound){
            throw new NoSuchElementException(notFound);
        }
    }
}
