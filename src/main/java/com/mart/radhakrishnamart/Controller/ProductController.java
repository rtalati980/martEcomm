package com.mart.radhakrishnamart.Controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mart.radhakrishnamart.Repository.CategoryRepository;
import com.mart.radhakrishnamart.Repository.ProductRepository;
import com.mart.radhakrishnamart.To.Product;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/product/api")
public class ProductController {

	@Autowired
	ProductRepository prdRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@GetMapping("/")
	ResponseEntity<List<Product>> getAllProduct() {
		List<Product> prd = prdRepository.findAll();
		return new ResponseEntity<>(prd, HttpStatus.OK);
	}

	@GetMapping("/id/{id}")
	ResponseEntity<Product> getById(@PathVariable long id) {
		Optional<Product> productOptional = prdRepository.findById(id);
		return productOptional.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

	}

	   @Value("${file.upload-dir}") 
	    private String uploadDir;

	   @GetMapping("/name/{name}")
		ResponseEntity<Product>  byName(@PathVariable String name){
			 Optional<Product> catOptional = Optional.of(prdRepository.findByName(name));
		        return catOptional.map( product-> new ResponseEntity<>(product, HttpStatus.OK))
		                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
			
		}
	   
	   
	   @GetMapping("/images/product_images/{imageName:.+}")
	    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
	        try {
	            // Load the image file as a Resource
	            Path imagePath = Paths.get(uploadDir, "product_images").resolve(imageName);
	            Resource resource = new UrlResource(imagePath.toUri());

	            // Check if the file exists and is readable
	            if (!resource.exists() || !resource.isReadable()) {
	                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	            }

	            return ResponseEntity.ok()
	                                 .contentType(MediaType.IMAGE_JPEG) // Set appropriate content type
	                                 .body(resource);
	        } catch (MalformedURLException e) {
	            // Handle invalid URL
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        } catch (Exception e) {
	            // Handle other exceptions
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	   
	    @PostMapping("/add")
	    public ResponseEntity<Product> createProduct(@RequestParam("name") String name, 
	                                                 @RequestParam("price") double price,
	                                                 @RequestParam("images") List<MultipartFile> images, 
	                                                 @RequestParam("description") String description,
	                                                 @RequestParam("category_id") Long categoryId) throws Exception {
	        Product product = new Product();
	        product.setName(name);
	        product.setPrice(price);
	        
	        product.setDiscription(description);
	        product.setCategory(categoryRepository.findById(categoryId).orElse(null));

	        if (images != null && !images.isEmpty()) {
	            List<String> imagePaths = new ArrayList<>();
	            for (MultipartFile img : images) {
	                // Define the directory path where you want to save the images
	                String directoryPath = uploadDir + File.separator + "product_images";

	                // Create the directory if it doesn't exist
	                File directory = new File(directoryPath);
	                if (!directory.exists()) {
	                    directory.mkdirs();
	                }

	                // Save the image to the directory
	                Path filePath = Paths.get(directoryPath, img.getOriginalFilename());
	                Files.copy(img.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	                // Add the image path to the list
	                String imagePath = "/product_images/" + img.getOriginalFilename();
	                imagePaths.add(imagePath);
	            }
	            product.setImages(imagePaths);
	        }

	        Product savedProduct = prdRepository.save(product);
	        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
	    }

	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(@PathVariable Long id,
	                                             @RequestParam("name") String name,
	                                             @RequestParam("price") double price,
	                                             
	                                             @RequestParam("discription") String description,
	                                             @RequestParam("images") List<MultipartFile> images,
	                                             @RequestParam("category_id") Long categoryId)
	                                             throws Exception {
	    Optional<Product> productOptional = prdRepository.findById(id);
	    if (!productOptional.isPresent()) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }

	    Product product = productOptional.get();
	    product.setName(name);
	    product.setPrice(price);
	   
	    product.setDiscription(description);
	    product.setCategory(categoryRepository.findById(categoryId).orElse(null));

	    if (images != null && !images.isEmpty()) {
	        List<String> imagePaths = new ArrayList<>();
	        for (MultipartFile img : images) {
	            String imagePath = "/uploads/" + img.getOriginalFilename();
	            imagePaths.add(imagePath);
	        }
	        product.setImages(imagePaths);
	    }

	    Product updatedProduct = prdRepository.save(product);
	    return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Product> deleteByID(@PathVariable Long id) {
		if (prdRepository.existsById(id)) {
			prdRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
