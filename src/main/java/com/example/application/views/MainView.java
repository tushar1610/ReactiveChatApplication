package com.example.application.views;

import com.example.application.ChatMessage;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

@Route
@StyleSheet("frontend://themes/vaadin-chat/styles.css")
public class MainView extends VerticalLayout {

    private final UnicastProcessor<ChatMessage> publisher;
    private final Flux<ChatMessage> messageFlux;
    private String username;

    public MainView(UnicastProcessor<ChatMessage> publisher,
                    Flux<ChatMessage> messageFlux){
        this.publisher = publisher;
        this.messageFlux = messageFlux;
        addClassName("main-view");
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        H1 header = new H1("Vaadin Chat");
        header.getElement().getThemeList().add("dark");
        add(header);

        askUsername();
    }

    private void askUsername() {
        HorizontalLayout layout = new HorizontalLayout();
        TextField usernameField = new TextField();
        Button startButton = new Button("Start Chat");
        layout.add(usernameField, startButton);
        startButton.addClickListener(buttonClickEvent -> {
            username = usernameField.getValue();
            remove(layout);
            showChat();
        });
        add(layout);
    }

    private void showChat() {
        MessageList messageList = new MessageList();

        add(messageList, createInputLayout());
        expand(messageList);

        messageFlux.subscribe(message -> {
            getUI().ifPresent(ui -> {
                ui.access(() -> {
                    messageList.add(
                            new Paragraph(message.getFrom() + " : " +
                                    message.getMessage())
                    );
                });
            });

        });
    }

    private Component createInputLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");
        TextField messageField  =new TextField();
        Button sendButton = new Button("Send");
        sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layout.add(messageField, sendButton);
        layout.expand(messageField);
        sendButton.addClickListener(buttonClickEvent -> {
            publisher.onNext(new ChatMessage(username, messageField.getValue()));
            messageField.clear();
            messageField.focus();
        });
        messageField.focus();
        return layout;
    }
}
