package enums;

public enum OrderStatus {
    PENDING("⏳ Kutilmoqda"),
    PROCESSING("🔄 Tayyorlanmoqda"),
    SHIPPED("🚚 Yo'lda"),
    DELIVERED("✅ Yetkazildi");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() { return description; }
}