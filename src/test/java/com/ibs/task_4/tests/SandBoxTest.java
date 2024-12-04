package com.ibs.task_4.tests;

import com.ibs.task_4.base_test.BaseTest;
import com.ibs.task_4.pages.AddProductModal;
import com.ibs.task_4.pages.GoodsPage;
import com.ibs.task_4.pages.SandBoxPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.*;
import java.time.Duration;

@DisplayName("добавление товара и удаление через БД")
public class SandBoxTest extends BaseTest {

    @DisplayName("добавление существующего товара")
    @Step("добавление существующего продукта")
    @Test
    void AddingExistingProduct() throws SQLException {

        SandBoxPage sandBoxPage = new SandBoxPage(driver);
        sandBoxPage.clickSandBoxLink().clickGoodsLink();

        GoodsPage goodsPage = new GoodsPage(driver);
        goodsPage.isListProductsHeaderDisplayed().clickAddButton();

        // Ожидаем, пока модальное окно "Добавление товара" станет видимым
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='editModalLabel']")));

        AddProductModal addProductModal = new AddProductModal(driver);
        addProductModal.isAddProductModalDisplayed().setProductName("Яблоко").selectTypeFruit()
                .clickSaveButton().isElementExists();

        // устанавливаем соединение с БД, передали УРЛ, логин и пароль
        Connection connection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:testdb",
                "user", "pass");

         //Проверка количества товаров в БД
        String query = "SELECT FOOD_NAME, COUNT(FOOD_NAME) AS COUNT FROM food " +
                "WHERE food_name = 'Яблоко' AND FOOD_TYPE = 'FRUIT' AND FOOD_EXOTIC = '0' GROUP BY FOOD_NAME";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            String name = resultSet.getString("FOOD_NAME");
            int count = resultSet.getInt("COUNT");
            System.out.printf("%s, %d%n", name, count);
        }

        // Удаление товара через БД
        String deleteQuery = "DELETE FROM food WHERE food_name = 'Яблоко';";
        int rowsDeleted = statement.executeUpdate(deleteQuery);
        System.out.println("Удалено строк: " + rowsDeleted);

        // Проверка количества товаров после удаления
        resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            int count = resultSet.getInt("COUNT");
            System.out.println("Кол-во яблок после удаления: " + count);
        } else {
            System.out.println("Кол-во яблок после удаления: " + 0);
        }

        connection.close();
    }
}
