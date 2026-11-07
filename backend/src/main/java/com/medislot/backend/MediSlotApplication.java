
package com.medislot.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MediSlotApplication {

    public static void main(String[] args) {

        SpringApplication.run(
            MediSlotApplication.class, args
        );

        System.out.println("==============================");
        System.out.println(" MediSlot Backend Started!");
        System.out.println("==============================");
    }
}
