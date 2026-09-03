package com.example.starter.dao;

import com.example.starter.domain.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductDaoImpl implements ProductDao {

    private final SessionFactory sessionFactory;

    public ProductDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<Product> findAll() {
        return session().createQuery("from Product p order by p.id", Product.class).list();
    }

    @Override
    public List<Product> findByNameContaining(String keyword) {
        return session()
                .createQuery("from Product p where lower(p.name) like lower(:kw) order by p.id", Product.class)
                .setParameter("kw", "%" + keyword + "%")
                .list();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(session().get(Product.class, id));
    }

    @Override
    public Product save(Product product) {
        session().persist(product);
        return product;
    }

    @Override
    public void delete(Product product) {
        session().remove(product);
    }

    @Override
    public long count() {
        Long count = session().createQuery("select count(p) from Product p", Long.class).uniqueResult();
        return count == null ? 0L : count;
    }
}
