package controllers;
import bot.EcommerceBot;
import enums.Category;
import enums.UserState;
import models.Order;
import models.Product;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import pojo.UserSession;
import services.OrderService;
import services.ProductService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CallbackQueryHandler {
    private final EcommerceBot bot;
    private final Map<Long, UserSession> sessions;
    private final ProductService productService;
    private final OrderService orderService;

    public CallbackQueryHandler(EcommerceBot bot, Map<Long, UserSession> sessions,
                                ProductService productService, OrderService orderService) {
        this.bot = bot;
        this.sessions = sessions;
        this.productService = productService;
        this.orderService = orderService;
    }

    public void handleCallback(org.telegram.telegrambots.meta.api.objects.CallbackQuery callback) {
        Long chatId = callback.getMessage().getChatId();
        String data = callback.getData();
        UserSession session = sessions.computeIfAbsent(chatId, k -> new UserSession(chatId));

        switch (data) {
            case "main":
                showMainMenu(chatId);
                break;
            case "products":
                showCategories(chatId);
                break;
            case "cart":
                showCart(chatId);
                break;
            case "orders":
                showOrders(chatId);
                break;
            case "about":
                showAbout(chatId);
                break;
            case "search":
                startSearch(chatId);
                break;
            case "electronics":
                showCategoryProducts(chatId, Category.ELECTRONICS);
                break;
            case "books":
                showCategoryProducts(chatId, Category.BOOKS);
                break;
            case "clothing":
                showCategoryProducts(chatId, Category.CLOTHING);
                break;
            default:
                if (data.startsWith("add_cart_")) {
                    addToCart(chatId, data.substring(9));
                } else if (data.startsWith("order_")) {
                    createOrder(chatId, data.substring(6));
                }
                break;
        }
    }

    private void showMainMenu(Long chatId) {
        InlineKeyboardMarkup keyboard = createMainMenuKeyboard();
        bot.sendMessage(chatId, "🏪 Bosh menu:", keyboard);
    }

    private void showCategories(Long chatId) {
        List<List<InlineKeyboardButton>> rows = Arrays.asList(
                Arrays.asList(
                        InlineKeyboardButton.builder().text("📱 Electronics").callbackData("electronics").build(),
                        InlineKeyboardButton.builder().text("📚 Books").callbackData("books").build()
                ),
                Arrays.asList(
                        InlineKeyboardButton.builder().text("👕 Clothing").callbackData("clothing").build(),
                        InlineKeyboardButton.builder().text("🏠 Bosh menu").callbackData("main").build()
                )
        );

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder().keyboard(rows).build();
        bot.sendMessage(chatId, "📦 Kategoriyalarni tanlang:", keyboard);
    }

    private void showCategoryProducts(Long chatId, Category category) {
        List<Product> products = productService.getProductsByCategory(category);
        showProducts(chatId, products, "📦 " + category.name() + " mahsulotlari:");
    }

    private void showCart(Long chatId) {
        UserSession session = sessions.get(chatId);
        List<Product> cartItems = session.getCart();

        if (cartItems.isEmpty()) {
            bot.sendMessage(chatId, "🛒 Savat bo'sh!", createMainMenuKeyboard());
            return;
        }

        StringBuilder text = new StringBuilder("🛒 Savatingiz:\n\n");
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        double total = 0;

        for (Product product : cartItems) {
            text.append(String.format("• %s - %s $\n", product.getName(), product.getPrice()));
            total += product.getPrice();
            rows.add(Arrays.asList(
                    InlineKeyboardButton.builder()
                            .text("📦 " + product.getName() + " buyurtma berish")
                            .callbackData("order_" + product.getId())
                            .build()
            ));
        }

        text.append(String.format("\n💰 Jami: %.2f $", total));
        rows.add(Arrays.asList(
                InlineKeyboardButton.builder().text("🏠 Bosh menu").callbackData("main").build()
        ));

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder().keyboard(rows).build();
        bot.sendMessage(chatId, text.toString(), keyboard);
    }

    private void showOrders(Long chatId) {
        List<Order> orders = orderService.getUserOrders(chatId);

        if (orders.isEmpty()) {
            bot.sendMessage(chatId, "📋 Buyurtmalar yo'q!", createMainMenuKeyboard());
            return;
        }

        StringBuilder text = new StringBuilder("📋 Buyurtmalaringiz:\n\n");
        for (Order order : orders) {
            text.append(String.format("🆔 #%d\n📦 %s\n📅 %s\n🚚 %s\n\n",
                    order.getId(), order.getProductName(), order.getOrderDate(),
                    order.getStatus().getDescription()));
        }

        bot.sendMessage(chatId, text.toString(), createMainMenuKeyboard());
    }

    private void showAbout(Long chatId) {
        String aboutText = """
            ℹ️ Biz haqimizda
            
            🏪 Bizning do'kon - O'zbekistondagi eng yaxshi texnologiya do'koni!
            
            📱 Biz taklif qilamiz:
            • Eng so'nggi telefonlar
            • Yuqori sifatli noutbuklar
            • Turli xil aksessuarlar
            • Planshetlar
            
            🚚 Tez yetkazib berish
            💯 Sifat kafolati
            🎧 24/7 qo'llab-quvvatlash
            
            📞 Aloqa:
            Telefon: +998 90 515 55 48
            Email: eshboyevtemur0@gmail.com
            """;

        bot.sendMessage(chatId, aboutText, createMainMenuKeyboard());
    }

    private void startSearch(Long chatId) {
        UserSession session = sessions.get(chatId);
        session.setState(UserState.SEARCHING);
        bot.sendMessage(chatId, "🔍 Qidirishni boshlang. Mahsulot nomini yozing:", null);
    }

    private void addToCart(Long chatId, String productId) {
        Product product = productService.getProductById(Long.parseLong(productId));
        if (product != null) {
            UserSession session = sessions.get(chatId);
            session.addToCart(product);
            bot.sendMessage(chatId, "✅ " + product.getName() + " savatga qo'shildi!",
                    createMainMenuKeyboard());
        }
    }

    private void createOrder(Long chatId, String productId) {
        Product product = productService.getProductById(Long.parseLong(productId));
        if (product != null) {
            Order order = orderService.createOrder(chatId, product);
            bot.sendMessage(chatId,
                    String.format("✅ Buyurtma yaratildi!\n🆔 #%d\n📦 %s\n📅 %s",
                            order.getId(), product.getName(), order.getOrderDate()),
                    createMainMenuKeyboard());
        }
    }

    private void showProducts(Long chatId, List<Product> products, String title) {
        StringBuilder text = new StringBuilder(title + "\n\n");
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Product product : products) {
            text.append(String.format("📱 %s\n💰 %s $\n\n",
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
}