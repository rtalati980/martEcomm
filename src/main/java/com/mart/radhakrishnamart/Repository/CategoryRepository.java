package com.mart.radhakrishnamart.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.radhakrishnamart.To.Category;


public interface CategoryRepository extends JpaRepository<Category, Long> {
	      
	Category findByName(String name);
}
