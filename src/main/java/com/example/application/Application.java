package com.example.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "vaadin-chat")
@Push //This annotation should be on an AppShellConfigurator implementation class only.
// Cannot be on a non AppShellConfigurator class.
// If found It will throw an error.
//According to previous version it should be on MainView.java
//But since newer version it should be on an AppShellConfigurator implementation class.
//The use of this annotation is to enable Websocket related config which helps two way configuration
//If not found, the first UI using the application will be able to send the message and others will be able to see
//But when other UI starts sending the message, no one will be able to see.
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    UnicastProcessor<ChatMessage> publisher(){
        return UnicastProcessor.create();
    }

    @Bean
    Flux<ChatMessage> messageFlux(UnicastProcessor<ChatMessage> publisher){
        return publisher.replay(30).autoConnect();
    }



}
