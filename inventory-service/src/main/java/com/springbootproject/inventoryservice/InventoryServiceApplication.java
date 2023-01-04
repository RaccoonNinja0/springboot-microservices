package com.springbootproject.inventoryservice;

import com.springbootproject.inventoryservice.model.Inventory;
import com.springbootproject.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository){
		return args -> {
			Inventory inventoryA = new Inventory();
			inventoryA.setSkuCode("red car");
			inventoryA.setQuantity(10);

			Inventory inventoryB = new Inventory();
			inventoryB.setSkuCode("blue car");
			inventoryB .setQuantity(2);

			inventoryRepository.save(inventoryA);
			inventoryRepository.save(inventoryB);
		};
	}
}
