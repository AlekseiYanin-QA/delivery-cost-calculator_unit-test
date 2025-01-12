package com.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryCostCalculatorTest {

    private DeliveryCostCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new DeliveryCostCalculator();
    }

    @Nested
    @DisplayName("Тесты валидации входных параметров")
    @Tag("Validation") // Тесты для проверки валидации входных данных
    class ValidationTests {

        @Test
        @DisplayName("Отрицательное расстояние должно вызывать исключение")
        @Tag("NegativeDistance") // Тест на отрицательное расстояние
        void shouldThrowExceptionForNegativeDistance() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> calculator.calculateDeliveryCost(-1.0, Size.SMALL, false, Load.NORMAL)
            );
            assertEquals("Расстояние не может быть отрицательным", exception.getMessage());
        }

        @Test
        @DisplayName("Хрупкий груз на расстояние более 30 км должен вызывать исключение")
        @Tag("FragileDistance") // Тест на хрупкий груз и максимальное расстояние
        void shouldThrowExceptionForFragileItemOverMaxDistance() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> calculator.calculateDeliveryCost(31.0, Size.SMALL, true, Load.NORMAL)
            );
            assertEquals("Хрупкие грузы нельзя доставлять на расстояние более 30 км", exception.getMessage());
        }

        @ParameterizedTest
        @DisplayName("Null значения параметров должны вызывать исключение")
        @Tag("NullParameters") // Тест на проверку null-параметров
        @MethodSource("com.example.DeliveryCostCalculatorTest#nullParametersProvider")
        void shouldThrowExceptionForNullParameters(Double distance, Size size, Boolean isFragile, Load loadLevel) {
            assertThrows(IllegalArgumentException.class,
                    () -> calculator.calculateDeliveryCost(distance, size, isFragile, loadLevel)
            );
        }
    }

    @Nested
    @DisplayName("Тесты расчета стоимости")
    @Tag("CostCalculation") // Тесты для проверки расчёта стоимости доставки
    class CostCalculationTests {

        @ParameterizedTest
        @DisplayName("Базовый расчет стоимости для разных расстояний")
        @Tag("BaseCost") // Тесты базовой стоимости в зависимости от расстояния
        @CsvSource({
                "1.0, SMALL, false, NORMAL, 400.0",   // Минимальная стоимость
                "3.0, SMALL, false, NORMAL, 400.0",   // От 2 до 10 км
                "15.0, SMALL, false, NORMAL, 400.0",  // От 10 до 30 км
                "35.0, SMALL, false, NORMAL, 400.0"   // Более 30 км
        })
        void shouldCalculateBaseCost(double distance, Size size, boolean isFragile, Load loadLevel, double expected) {
            assertEquals(expected, calculator.calculateDeliveryCost(distance, size, isFragile, loadLevel),
                    "Неверная базовая стоимость для расстояния: " + distance);
        }

        @ParameterizedTest
        @DisplayName("Расчет стоимости с учетом размера груза")
        @Tag("SizeCost") // Тесты на стоимость в зависимости от размера груза
        @CsvSource({
                "15.0, SMALL, false, NORMAL, 400.0",  // Маленький размер (базовая цена 200 + 100)
                "15.0, LARGE, false, NORMAL, 400.0"   // Большой размер (базовая цена 200 + 200)
        })
        void shouldCalculateSizeCost(double distance, Size size, boolean isFragile, Load loadLevel, double expected) {
            assertEquals(expected, calculator.calculateDeliveryCost(distance, size, isFragile, loadLevel),
                    "Неверная стоимость для размера: " + size);
        }

        @ParameterizedTest
        @DisplayName("Расчет стоимости для хрупких грузов")
        @Tag("FragileCost") // Тесты на стоимость для хрупких грузов
        @CsvSource({
                "15.0, SMALL, true, NORMAL, 600.0",   // Хрупкий груз (базовая цена 200 + 100 + 300 за хрупкость)
                "15.0, SMALL, false, NORMAL, 400.0"   // Обычный груз (базовая цена 200 + 100)
        })
        void shouldCalculateFragileCost(double distance, Size size, boolean isFragile, Load loadLevel, double expected) {
            assertEquals(expected, calculator.calculateDeliveryCost(distance, size, isFragile, loadLevel),
                    "Неверная стоимость для хрупкости: " + isFragile);
        }

        @ParameterizedTest
        @DisplayName("Расчет стоимости с учетом уровня загрузки")
        @Tag("LoadCost") // Тесты на стоимость с учётом уровня загрузки
        @CsvSource({
                "15.0, SMALL, false, NORMAL, 400.0",     // Нормальная загрузка (x1.0)
                "15.0, SMALL, false, INCREASED, 400.0",  // Повышенная загрузка (x1.2)
                "15.0, SMALL, false, HIGH, 420.0",       // Высокая загрузка (x1.4)
                "15.0, SMALL, false, VERY_HIGH, 480.0"   // Очень высокая загрузка (x1.6)
        })
        void shouldCalculateLoadCost(double distance, Size size, boolean isFragile, Load loadLevel, double expected) {
            assertEquals(expected, calculator.calculateDeliveryCost(distance, size, isFragile, loadLevel),
                    "Неверная стоимость для уровня загрузки: " + loadLevel);
        }
    }

    @Nested
    @DisplayName("Комплексные тесты")
    @Tag("ComplexTests") // Комплексные тесты, проверяющие несколько аспектов одновременно
    class ComplexTests {

        @ParameterizedTest
        @DisplayName("Комбинированные случаи расчета стоимости")
        @Tag("ComplexCases") // Тесты для комбинированных случаев
        @MethodSource("com.example.DeliveryCostCalculatorTest#complexTestCasesProvider")
        void shouldCalculateComplexCases(double distance, Size size, boolean isFragile,
                                         Load loadLevel, double expected) {
            assertEquals(expected, calculator.calculateDeliveryCost(distance, size, isFragile, loadLevel),
                    "Неверная стоимость для комбинированного случая");
        }
    }

    // Провайдеры тестовых данных
    private static Stream<Arguments> nullParametersProvider() {
        return Stream.of(
                Arguments.of(null, Size.SMALL, false, Load.NORMAL),
                Arguments.of(15.0, null, false, Load.NORMAL),
                Arguments.of(15.0, Size.SMALL, null, Load.NORMAL),
                Arguments.of(15.0, Size.SMALL, false, null)
        );
    }

    private static Stream<Arguments> complexTestCasesProvider() {
        return Stream.of(
                // Расстояние, Размер, Хрупкость, Загрузка, Ожидаемая стоимость
                // Формула: (базовая + размер + хрупкость) * коэффициент загрузки

                // (300 + 200 + 0) * 1.6 = 800
                Arguments.of(35.0, Size.LARGE, false, Load.VERY_HIGH, 800.0),

                // (250 + 100 + 300) * 1.4 = 910
                Arguments.of(5.0, Size.SMALL, true, Load.HIGH, 910.0),

                // (200 + 200 + 0) * 1.2 = 480
                Arguments.of(25.0, Size.LARGE, false, Load.INCREASED, 480.0),

                // max((100 + 100 + 0) * 1.0, 400) = 400
                Arguments.of(1.0, Size.SMALL, false, Load.NORMAL, 400.0),

                // (200 + 200 + 300) * 1.0 = 700
                Arguments.of(30.0, Size.LARGE, true, Load.NORMAL, 700.0)
        );
    }
}