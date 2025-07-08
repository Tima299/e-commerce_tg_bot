package bot;

import controllers.CallbackQueryHandler;
import controllers.CommandHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import pojo.UserSession;
import services.OrderService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import services.ProductService;

public class EcommerceBot extends TelegramLongPollingBot {
    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();
    private final ProductService productService = new ProductService();
    private final OrderService orderService = new OrderService();

    @Override
    public String getBotUsername() { return "@TemurkhansBot"; }

    @Override
    public String getBotToken() { return "7608575625:AAFSVTZIbYhWBB3R69HwF9nMrsuKQfgAUgE"; }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            new CommandHandler(this, sessions, productService, orderService)
                    .handleCommand(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            new CallbackQueryHandler(this, sessions, productService, orderService)
                    .handleCallback(update.getCallbackQuery());
        }
    }

    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        try {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text)
                    .replyMarkup(keyboard)
                    .build();
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void clearWebhook(){
        // This method is intentionally left empty to avoid setting a webhook
    }

}
