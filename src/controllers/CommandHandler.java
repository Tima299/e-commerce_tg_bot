package controllers;
import bot.EcommerceBot;
import enums.UserState;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import pojo.UserSession;
import services.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import models.*;

public class CommandHandler {
    private final EcommerceBot bot;
    private final Map<Long, UserSession> sessions;
    private final ProductService productService;
    private final OrderService orderService;


    public CommandHandler(EcommerceBot bot, Map<Long, UserSession> sessions,
                          ProductService productService, OrderService orderService) {
        this.bot = bot;
        this.sessions = sessions;
        this.productService = productService;
        this.orderService = orderService;
    }

    public void handleCommand(org.telegram.telegrambots.meta.api.objects.Message message) {
        if (message == null || message.getText() == null) {
            return;
        }
        Long chatId = message.getChatId();
        String text = message.getText();

        UserSession session = sessions.computeIfAbsent(chatId, k -> new UserSession(chatId));

        if (text.equals("/start") || text.equals("🏠 Bosh menu")) {
            showMainMenu(chatId);
            session.setState(UserState.MAIN_MENU);
        } else if (session.getState() == UserState.SEARCHING) {
            handleSearch(chatId, text);
        } else {
            showMainMenu(chatId);
        }
    }

    private void showMainMenu(Long chatId) {
        InlineKeyboardMarkup keyboard = createMainMenuKeyboard();
        bot.sendMessage(chatId, "🏪 Xush kelibsiz! Quyidagi bo'limlardan birini tanlang:", keyboard);
    }

    private InlineKeyboardMarkup createMainMenuKeyboard() {
        List<List<InlineKeyboardButton>> rows = Arrays.asList(
                Arrays.asList(
                        InlineKeyboardButton.builder().text("📦 Mahsulotlar").callbackData("products").build(),
                        InlineKeyboardButton.builder().text("🛒 Savatim").callbackData("cart").build()
                ),
                Arrays.asList(
                        InlineKeyboardButton.builder().text("📋 Buyurtmalarim").callbackData("orders").build(),
                        InlineKeyboardButton.builder().text("ℹ️ Biz haqimizda").callbackData("about").build()
                ),
                Arrays.asList(
                        InlineKeyboardButton.builder().text("🔍 Qidirish").callbackData("search").build(),
                        InlineKeyboardButton.builder().text("🏠 Bosh menu").callbackData("main").build()
                )
        );
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    private void handleSearch(Long chatId, String query) {
        List<Product> results = productService.searchProducts(query);
        UserSession session = sessions.get(chatId);

        if (results.isEmpty()) {
            bot.sendMessage(chatId, "❌ Mahsulot topilmadi!", createMainMenuKeyboard());
            session.setState(UserState.MAIN_MENU);
        } else {
            showProducts(chatId, results, "🔍 Qidiruv natijalari:");
            session.setState(UserState.BROWSING_PRODUCTS);
        }
    }

    private void showProducts(Long chatId, List<Product> products, String title) {
        StringBuilder text = new StringBuilder(title + "\n\n");
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Product product : products) {
            text.append(String.format("📱 %s\n💰 %s so'm\n\n",
                    product.getName(), product.getPrice()));
            rows.add(Arrays.asList(
                    InlineKeyboardButton.builder()
                            .text("❤️ " + product.getName())
                            .callbackData("add_cart_" + product.getId())
                            .build()
            ));
        }

        rows.add(Arrays.asList(
                InlineKeyboardButton.builder().text("🏠 Bosh menu").callbackData("main").build()
        ));

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder().keyboard(rows).build();
        bot.sendMessage(chatId, text.toString(), keyboard);
    }
}
