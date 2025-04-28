package product_service.service;

import product_service.event.KafkaProducer;
import product_service.event.ProductCreatedEvent;
import product_service.model.Product;
import product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final KafkaProducer kafkaProducer;

    public ProductService(ProductRepository repository, KafkaProducer kafkaProducer) {
        this.repository = repository;
        this.kafkaProducer = kafkaProducer;
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Product getProductById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Product saveProduct(Product product) {
        Product savedProduct = repository.save(product);

        // Publish Kafka event
        ProductCreatedEvent event = new ProductCreatedEvent(
                savedProduct.getSkuCode(),
                savedProduct.getStock(),
                savedProduct.getPrice()
        );
        kafkaProducer.sendProductCreatedEvent(event);
        return savedProduct;
    }

    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }

    public boolean isProductExists(String skuCode) {
        return repository.findBySkuCode(skuCode).isPresent();
    }

}
