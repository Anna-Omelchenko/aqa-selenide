package ru.netology.aqa.selenide;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            .withZone(ZoneId.systemDefault());

    @Test
    void testSuccessWithDirectInput() {
        open("http://localhost:9999");
        SelenideElement form = $(".form");
        String cityName = "Тверь";
        form.$("[data-test-id=city] input").setValue(cityName);
        $(".menu-item").find(byText(cityName)).click();
        String deliveryDate = DATE_FORMATTER.format(Instant.now().plus(5, ChronoUnit.DAYS));
        SelenideElement dateInput = form.$("[data-test-id=date] input");
        dateInput.sendKeys(Keys.CONTROL + "a");
        dateInput.sendKeys(Keys.DELETE);
        dateInput.sendKeys(deliveryDate);
        form.$("[data-test-id=name] input").setValue("Орлова-Тупова Марина");
        form.$("[data-test-id=phone] input").setValue("+79031234567");
        form.$("[data-test-id=agreement]").click();
        form.$(".button").click();
        $("[data-test-id=notification]").shouldBe(visible, Duration.ofSeconds(15));
        SelenideElement notification = $("[data-test-id=notification]");
        notification.$(byClassName("notification__title")).shouldHave(exactText("Успешно!"));
        notification.$(byClassName("notification__content"))
                .shouldHave(text("Встреча успешно забронирована на " + deliveryDate));
    }

    @Test
    void testSuccessWithWidgets() {
        open("http://localhost:9999");
        SelenideElement form = $(".form");
        // Select city
        form.$("[data-test-id=city] input").sendKeys("Вл");
        $$(".menu-item").find(exactText("Владимир")).click();
        // Select delivery date
        form.$("[data-test-id=date] button").click();
        Instant deliveryInstant = Instant.now().plus(7, ChronoUnit.DAYS);
        LocalDate deliveryDate = LocalDate.ofInstant(deliveryInstant, ZoneId.systemDefault());
        if (deliveryDate.getMonthValue() != LocalDate.now().getMonthValue()) {
            $$(".calendar__arrow_direction_right")
                    .find(Condition.attribute("data-step", "1")).click();
        }
        String deliveryDay = String.valueOf(deliveryDate.getDayOfMonth());
        $$(".calendar__day").find(exactText(deliveryDay)).click();
        // Input other fields
        form.$("[data-test-id=name] input").setValue("Орлова-Тупова Марина");
        form.$("[data-test-id=phone] input").setValue("+79031234567");
        form.$("[data-test-id=agreement]").click();
        form.$(".button").click();
        $("[data-test-id=notification]").shouldBe(visible, Duration.ofSeconds(15));
        SelenideElement notification = $("[data-test-id=notification]");
        notification.$(byClassName("notification__title")).shouldHave(exactText("Успешно!"));
        notification.$(byClassName("notification__content"))
                .shouldHave(text("Встреча успешно забронирована на " + DATE_FORMATTER.format(deliveryInstant)));
    }
}
