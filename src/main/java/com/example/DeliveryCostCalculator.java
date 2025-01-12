package com.example;

/**
 * Калькулятор стоимости доставки грузов.
 */
public class DeliveryCostCalculator {

    /**
     * Рассчитывает стоимость доставки.
     *
     * @param distance  Расстояние до пункта назначения в км.
     * @param size      Размер груза.
     * @param isFragile Флаг хрупкости груза.
     * @param loadLevel Уровень загруженности службы доставки.
     * @return Стоимость доставки.
     * @throws IllegalArgumentException Если входные параметры некорректны.
     */
    public double calculateDeliveryCost(Double distance, Size size, Boolean isFragile, Load loadLevel) {
        validateInputParameters(distance, size, isFragile, loadLevel);
        validateDistance(distance, isFragile);

        double baseCost = calculateBaseCost(distance);
        double sizeCost = calculateSizeCost(size);
        double fragileCost = calculateFragileCost(isFragile);

        double subtotal = baseCost + sizeCost + fragileCost;
        double totalCost = applyLoadMultiplier(subtotal, loadLevel);

        // Округляем до 2 знаков после запятой
        totalCost = Math.round(totalCost * 100.0) / 100.0;

        return Math.max(totalCost, DeliveryConstants.MINIMUM_COST.getValue());
    }

    /**
     * Проверяет входные параметры на null.
     */
    private void validateInputParameters(Double distance, Size size, Boolean isFragile, Load loadLevel) {
        if (distance == null || size == null || isFragile == null || loadLevel == null) {
            throw new IllegalArgumentException("Все параметры должны быть указаны (не могут быть null)");
        }
    }

    /**
     * Проверяет корректность расстояния.
     */
    private void validateDistance(double distance, boolean isFragile) {
        if (distance < 0) {
            throw new IllegalArgumentException("Расстояние не может быть отрицательным");
        }
        if (isFragile && distance > DeliveryConstants.MAX_FRAGILE_DISTANCE.getValue()) {
            throw new IllegalArgumentException(
                    String.format("Хрупкие грузы нельзя доставлять на расстояние более %.0f км",
                            DeliveryConstants.MAX_FRAGILE_DISTANCE.getValue())
            );
        }
    }

    /**
     * Рассчитывает базовую стоимость в зависимости от расстояния.
     */
    private double calculateBaseCost(double distance) {
        if (distance > 30) {
            return DeliveryConstants.BASE_COST_OVER_30_KM.getValue();
        } else if (distance > 10) {
            return DeliveryConstants.BASE_COST_10_TO_30_KM.getValue();
        } else if (distance > 2) {
            return DeliveryConstants.BASE_COST_2_TO_10_KM.getValue();
        }
        return DeliveryConstants.BASE_COST_UP_TO_2_KM.getValue();
    }

    /**
     * Рассчитывает стоимость в зависимости от размера груза.
     */
    private double calculateSizeCost(Size size) {
        return size == Size.LARGE
                ? DeliveryConstants.LARGE_SIZE_COST.getValue()
                : DeliveryConstants.SMALL_SIZE_COST.getValue();
    }

    /**
     * Рассчитывает стоимость за хрупкость груза.
     */
    private double calculateFragileCost(boolean isFragile) {
        return isFragile
                ? DeliveryConstants.FRAGILE_COST.getValue()
                : 0.0;
    }

    /**
     * Применяет коэффициент загруженности к стоимости.
     */
    private double applyLoadMultiplier(double cost, Load loadLevel) {
        return cost * switch (loadLevel) {
            case VERY_HIGH -> 1.6;
            case HIGH -> 1.4;
            case INCREASED -> 1.2;
            case NORMAL -> 1.0;
        };
    }
}