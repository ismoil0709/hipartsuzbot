package uz.hiparts.hipartsuz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class HipartsuzApplication {
    private static final String NGROK_URL = "loved-basilisk-magnetic.ngrok-free.app";
    public static void main(String[] args) throws IOException {
        new ProcessBuilder("/snap/bin/ngrok", "http", "--domain=" + NGROK_URL, "8080").start();
        SpringApplication.run(HipartsuzApplication.class,args);
    }
}
