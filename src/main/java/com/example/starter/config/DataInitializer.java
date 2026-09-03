package com.example.starter.config;

import com.example.starter.dao.ProductDao;
import com.example.starter.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

@Component
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ProductDao productDao;
    private final TransactionTemplate transactionTemplate;
    private boolean initialized;

    public DataInitializer(ProductDao productDao, PlatformTransactionManager transactionManager) {
        this.productDao = productDao;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (initialized) {
            return;
        }
        // Root context only (avoid double-run from servlet context)
        if (event.getApplicationContext().getParent() != null) {
            return;
        }

        transactionTemplate.executeWithoutResult(status -> {
            if (productDao.count() == 0) {
                productDao.save(sample("Notebook", "A5 hardcover notebook", "3.50", 100));
                productDao.save(sample("USB Cable", "USB-C 1m cable", "7.99", 50));
                productDao.save(sample("Wireless Mouse", "2.4GHz optical mouse", "15.00", 25));
                log.info("Inserted sample products");
            }
        });
        initialized = true;
    }

    private Product sample(String name, String description, String price, int quantity) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setQuantity(quantity);
        return product;
    }
}
