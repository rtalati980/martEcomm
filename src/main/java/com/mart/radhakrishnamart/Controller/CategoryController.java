package com.mart.radhakrishnamart.Controller;

import com.azure.storage.blob.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mart.radhakrishnamart.Repository.CategoryRepository;
import com.mart.radhakrishnamart.To.Category;
import com.mart.radhakrishnamart.To.Product;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/category/api")
public class CategoryController {

	private final BlobServiceClient blobServiceClient;
    private final CategoryRepository categoryRepository;

	
    @Autowired
    public CategoryController(BlobServiceClient blobServiceClient, CategoryRepository categoryRepository) {
        this.blobServiceClient = blobServiceClient;
        this.categoryRepository = categoryRepository;
    }
	
	
	
	 @Autowired
	  private ResourceLoader resourceLoader;
	 
	// @Value("${file.upload-dir}") // This will inject the directory path from application.properties
	//    private String uploadDir;   
	 
	/*
	 * @GetMapping("/images/{imageName:.+}") public ResponseEntity<Resource>
	 * getImage(@PathVariable String imageName) { try { // Load the image file as a
	 * Resource Path imagePath = Paths.get(uploadDir, "images").resolve(imageName);
	 * Resource resource = new UrlResource(imagePath.toUri());
	 * 
	 * // Check if the file exists and is readable if (!resource.exists() ||
	 * !resource.isReadable()) { return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 * }
	 * 
	 * return ResponseEntity.ok() .contentType(MediaType.IMAGE_JPEG) // Set
	 * appropriate content type .body(resource); } catch (MalformedURLException e) {
	 * // Handle invalid URL return new
	 * ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); } catch (Exception e) {
	 * // Handle other exceptions return new
	 * ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); } }
	 */
	 @GetMapping("/images/{imageName:.+}")
	    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
		 try {
		        // Construct the URL for the image in Azure Blob Storage
		        String blobUrl = "https://martimages.blob.core.windows.net/imagesmart/" + imageName;

		        // Create a Resource object with the image URL
		        UrlResource resource = new UrlResource(blobUrl);

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
	 
	 


	 
	 @PostMapping("/save")
	    public ResponseEntity<Category> saveCat(@RequestParam("name") String name, @RequestParam("images") MultipartFile images) throws IOException {
	        try {
	            Category category = new Category();
	            category.setName(name);

	            if (images != null && !images.isEmpty()) {
	                // Upload image to Azure Blob Storage
	                BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("imagesmart");
	                BlobClient blobClient = containerClient.getBlobClient(images.getOriginalFilename());
	                blobClient.upload(images.getInputStream(), images.getSize(), true);

	                // Set the image URL in the category object
	                category.setImgPath(blobClient.getBlobUrl());
	            }
	            
	            System.out.print( "Path to image"  +  category.getImgPath());

	            Category savedCat = categoryRepository.save(category);
	            return new ResponseEntity<>(savedCat, HttpStatus.CREATED);
	        } catch (Exception e) {
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	

    // Create the directory if it doesn't exist
 

     
	@GetMapping("/")
	public ResponseEntity<List<Category>> getAllCategory(){
		List<Category> catList=categoryRepository.findAll();
		return new ResponseEntity<>(catList, HttpStatus.OK);
	}

	@GetMapping("/id/{id}")
	ResponseEntity<Category>  getById(@PathVariable long id){
		 Optional<Category> catOptional = categoryRepository.findById(id);
	        return catOptional.map(category -> new ResponseEntity<>(category, HttpStatus.OK))
	                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
		
	}
	
	@GetMapping("/name/{name}")
	ResponseEntity<Category>  byName(@PathVariable String name){
		 Optional<Category> catOptional = Optional.of(categoryRepository.findByName(name));
	        return catOptional.map(category -> new ResponseEntity<>(category, HttpStatus.OK))
	                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
		
	}
	
	@PutMapping("/id/{id}")
    public ResponseEntity<Category> updateProduct(@PathVariable Long id,
                                                  @RequestParam("name") String name,
                                                  @RequestParam("images") MultipartFile images)
                                                  throws Exception {
        Optional<Category> catOptional = categoryRepository.findById(id);
        if (!catOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

       Category cat = catOptional.get();
        cat.setName(name);  
        if (images != null) {
	    	 cat.setImgPath(("/uploads/" + images.getOriginalFilename()));
	    	 
	     }

        Category updatedCat = categoryRepository.save(cat);
        return new ResponseEntity<>(updatedCat, HttpStatus.OK);
    }
	

	
	@DeleteMapping("/{id}")
	public ResponseEntity<Product> deleteByID(@PathVariable Long id){
		if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
	}
}
