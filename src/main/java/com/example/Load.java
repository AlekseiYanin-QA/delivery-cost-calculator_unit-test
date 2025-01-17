package com.example;
/**
 * Перечисление, представляющее уровень загруженности службы доставки.
 * Используется для расчета стоимости доставки с учетом текущей загруженности.
 */
public enum Load {
    VERY_HIGH, // Очень высокая загруженность
    HIGH,      // Высокая загруженность
    INCREASED, // Повышенная загруженность
    NORMAL     // Обычная загруженность
}
