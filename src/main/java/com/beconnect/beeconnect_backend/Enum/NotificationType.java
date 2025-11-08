package com.beconnect.beeconnect_backend.Enum;

public enum NotificationType {
    BEEKEEPER_VERIFIED,        // Zatwierdzenie wniosku pszczelarza
    BEEKEEPER_REJECTED,        // Odrzucenie wniosku pszczelarza
    AREA_RESERVED,             // Rezerwacja Twojego obszaru
    RESERVATION_CONFIRMED,     // Potwierdzenie Twojej rezerwacji
    RESERVATION_CANCELLED,     // Anulowanie rezerwacji
    NEW_MESSAGE,               // Nowa wiadomość w chacie
    NEW_ORDER,                 // Nowe zamówienie Twojego produktu
    ORDER_SHIPPED,             // Wysłanie zamówienia
    PRODUCT_REVIEW,            // Nowa recenzja produktu
    SYSTEM                     // Powiadomienie systemowe
}