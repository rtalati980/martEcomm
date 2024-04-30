package com.mart.radhakrishnamart.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.radhakrishnamart.To.Product;





public interface ProductRepository extends JpaRepository<Product, Long> {

	 Product findByName(String name);
}
