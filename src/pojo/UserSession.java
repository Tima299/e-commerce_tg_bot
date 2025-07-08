package pojo;


import enums.UserState;
import models.Product;

import java.util.ArrayList;
import java.util.List;

public class UserSession {
    private final Long chatId;
    private UserState state;
    private final List<Product> cart;

    public UserSession(Long chatId) {
        this.chatId = chatId;
        this.state = UserState.MAIN_MENU;
        this.cart = new ArrayList<>();
    }

    public Long getChatId() { return chatId; }
    public UserState getState() { return state; }
    public void setState(UserState state) { this.state = state; }
    public List<Product> getCart() { return cart; }
    public void addToCart(Product product) { cart.add(product); }
    public void clearCart() { cart.clear(); }
}