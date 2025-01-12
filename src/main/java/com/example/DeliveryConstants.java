package com.example;

/**
 * Перечисление для хранения констант, связанных с расчетом стоимости доставки.
 */
public enum DeliveryConstants {
    // Стоимость доставки хрупкого груза
    FRAGILE_COST(300.0),

    // Минимальная стоимость доставки
    MINIMUM_COST(400.0),

    // Максимальное расстояние для доставки хрупких грузов (в км)
    MAX_FRAGILE_DISTANCE(30.0),

    // Стоимость доставки для груза с большими габаритами
    LARGE_SIZE_COST(200.0),

    // Стоимость доставки для груза с маленькими габаритами
    SMALL_SIZE_COST(100.0),

    // Базовая стоимость доставки для расстояния более 30 км
    BASE_COST_OVER_30_KM(300.0),

    // Базовая стоимость доставки для расстояния от 10 до 30 км
    BASE_COST_10_TO_30_KM(200.0),

    // Базовая стоимость доставки для расстояния от 2 до 10 км
    BASE_COST_2_TO_10_KM(250.0),

    // Базовая стоимость доставки для расстояния до 2 км
    BASE_COST_UP_TO_2_KM(100.0);

    private final double value;


    DeliveryConstants(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}