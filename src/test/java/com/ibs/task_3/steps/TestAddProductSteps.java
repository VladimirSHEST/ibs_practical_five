package com.ibs.task_3.steps;

import com.ibs.task_3.base_test.BaseTest;
import com.ibs.task_3.pages.AddProductPage;
import com.ibs.task_3.pages.HomePage;
import com.ibs.task_3.pages.ProductsPage;
import io.cucumber.java.After;
import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestAddProductSteps extends BaseTest {

    private HomePage homePage;
    private ProductsPage productsPage;
    private AddProductPage addProductPage;

    @Дано("^открываем страницу с продуктами$")
    public void openLink() {
        driver.get("https://qualit.appline.ru/food");
        homePage = new HomePage(driver);
        homePage.clickSandboxLink().clickProductsLink();
        productsPage = new ProductsPage(driver);
        assertTrue(productsPage.isListProductsDisplayed(), "Список товаров не отображается");
    }

    @Когда("^нажимаем на кнопку \"Добавить товар\"$")
    public void clickAddProductButton() {
        productsPage.clickAddProductButton();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='editModalLabel']")));
        addProductPage = new AddProductPage(driver);
        assertTrue(addProductPage.isAddProductModalDisplayed(), "Модальное окно добавления товара не отображается");
    }

    @И("вводим название товара \"(.*)\"$")
    public void enterProductName(String productName) {
        addProductPage.enterProductName(productName);
    }

    @И("выбираем тип товара \"(.*)\"$")
    public void selectTypeProduct(String productType) {
        addProductPage.selectProductType(productType);
    }

    @И("^нажимаем кнопку \"Сохранить\"$")
    public void clickSaveButton() {
        addProductPage.ClickType().clickSaveButton();
    }

    // добавил ожидание появления элемента
    @Тогда("^товар \"(.*)\" добавлен в список$")
    public void productIsAddedList(String productName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        By productLocator = By.xpath("//th[.='5']/following-sibling::td[.='" + productName + "']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(productLocator));
        assertTrue(driver.findElement(productLocator).isDisplayed(), "Товар не добавлен");
    }

    @After
    public void tearDown() {
        super.tearDown();
    }
}


