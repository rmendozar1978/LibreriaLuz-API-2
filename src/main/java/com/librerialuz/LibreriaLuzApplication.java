package com.librerialuz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la API REST de Librería Luz.
 * Inicia el servidor HTTP y registra los componentes de la aplicación.
 *
 * @author Juan Sebastián Ávila Patiño
 */
@SpringBootApplication
public class LibreriaLuzApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibreriaLuzApplication.class, args);
    }
}
