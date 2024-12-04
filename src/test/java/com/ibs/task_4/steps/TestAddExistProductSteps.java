package com.ibs.task_4.steps;

import com.ibs.task_4.base_test.BaseTest;
import com.ibs.task_4.pages.AddProductModal;
import com.ibs.task_4.pages.GoodsPage;
import com.ibs.task_4.pages.SandBoxPage;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.*;
import java.time.Duration;

public class  TestAddExistProductSteps extends BaseTest {

    private SandBoxPage sandBoxPage;
    private GoodsPage goodsPage;
    private AddProductModal addProductModal;

    @Before
    public void setUp() {
        super.setUp();
        sandBoxPage = new SandBoxPage(driver);
        goodsPage = new GoodsPage(driver);
        addProductModal = new AddProductModal(driver);
    }

    @Дано("^находимся на вкладке \"Песочница\"$")
    public void sandboxTab() {
        sandBoxPage.clickSandBoxLink();
    }

    @Когда("^переход на вкладку \"Товары\"$")
    public void goTabProducts() {
        goodsPage = sandBoxPage.clickGoodsLink();
    }

    @И("^нажать кнопку \"Добавить\"$")
    public void clickButtonToAdd() {
        goodsPage.clickAddButton();
    }

    @И("^ввод название товара \"([^\"]*)\"$")
    public void enterProductName(String productName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='editModalLabel']")));
        addProductModal.setProductName(productName);
    }

    @И("^выбираем тип товара \"([^\"]*)\"$")
    public void selectTypeProduct(String type) {
        addProductModal.selectTypeFruit();
    }

    @И("^нажать кнопку \"Сохранить\"$")
    public void ClickSaveButton() {
        addProductModal.clickSaveButton();
    }

    @Тогда("^видим добавленный товар \"([^\"]*)\"$")
    public void seeAddedProduct(String productName) {
        addProductModal.isElementExists();
    }

    @И("^удалям товар \"([^\"]*)\" через БД$")
    public void removeProduct(String productName) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:testdb", "user", "pass");
        Statement statement = connection.createStatement();
        String deleteQuery = "DELETE FROM food WHERE food_name = '" + productName + "';";
        int rowsDeleted = statement.executeUpdate(deleteQuery);
        System.out.println("Удалено строк: " + rowsDeleted);
        connection.close();
    }

    @И("^проверка, что товар \"([^\"]*)\" удален$")
    public void checkRemoved(String productName) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:testdb", "user", "pass");
        Statement statement = connection.createStatement();
        String query = "SELECT COUNT(*) AS COUNT FROM food WHERE food_name = '" + productName + "';";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            int count = resultSet.getInt("COUNT");
            System.out.println("Кол-во товаров после удаления: " + count);
        }
        connection.close();
    }

    @After
    public void tearDown() {
        super.tearDown();
    }
}
