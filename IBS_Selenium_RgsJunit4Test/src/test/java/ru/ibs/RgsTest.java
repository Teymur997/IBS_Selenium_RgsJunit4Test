package ru.ibs;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RgsTest {
    WebDriver driver;
    WebDriverWait wait;
    Actions actions;
    JavascriptExecutor executor;

    @Before
    public void before() {
        System.setProperty("webdriver.gecko.driver", "src\\test\\resources\\geckodriver.exe");
        driver = new FirefoxDriver();
        actions = new Actions(driver);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        executor = (JavascriptExecutor)driver;
        wait = new WebDriverWait(driver, 10, 1000);
        driver.get("http://www.rgs.ru/");
    }
    @Test
    public void rgsTest() {
        //Проверить, загрузилась ли страница:
        String rgsHeaderPath = "//div[@class=\"logotype\"]";
        WebElement rgsHeader = driver.findElement(By.xpath(rgsHeaderPath));
        Assert.assertTrue("Страница не загружена", rgsHeader.isDisplayed());

        //Закрыть cookie:
        String cookieCloseButtonPath = "//button[@class=\"btn--text\"]";
        WebElement cookieCloseButton = driver.findElement(By.xpath(cookieCloseButtonPath));
        cookieCloseButton.click();
        Assert.assertTrue("\"cookie\" отображается на странице", cookieCloseButton.isDisplayed());

        //Выбрать Меню: Компаниям:
        String companyMenuPath = "//a[contains(text(), 'Компаниям')]";
        WebElement companyMenu = driver.findElement(By.xpath(companyMenuPath));
        companyMenu.click();

        //Закрыть всплывающее окно:
        String framePath = "//iframe[@id=\"fl-616371\"]";
        try {
            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.tagName("html"))));
            driver.switchTo().frame(driver.findElement(By.xpath(framePath)));
            String frameCloseButtonPath = "//div[@class=\"widget\"]//div[@class=\"widget__close js-collapse-login\"]";
            WebElement frameCloseButton = driver.findElement(By.xpath(frameCloseButtonPath));
            actions.click(frameCloseButton).perform();
        } catch (WebDriverException ex) {
            ex.getMessage();
        } finally {
            driver.switchTo().defaultContent();
            actions.click(companyMenu).perform();
        }
        //Проверить, был ли осуществлен переход в меню "Компаниям":
        Assert.assertTrue("Переход в меню \"Компаниям\" не осуществлен", companyMenu.getAttribute("class").contains("active"));

        //Выбрать Меню: Здоровье:
        String healthMenuPath = "//span[contains(text(), 'Здоровье')]";
        WebElement healthMenu = driver.findElement(By.xpath(healthMenuPath));
        healthMenu.click();

        //Проверить, был ли осуществлен переход в меню "Здоровье":
        WebElement healthMenuPresence = healthMenu.findElement(By.xpath("./.."));
        Assert.assertTrue("Переход в меню \"Здоровье\" не осуществлен", healthMenuPresence.getAttribute("class").contains("active"));


        //Выбрать Меню: Добровольное медицинское страхование:
        String voluntaryInsurancePath = "//a[contains(text(), 'Добровольное медицинское страхование')]";
        WebElement voluntaryInsurance = driver.findElement(By.xpath(voluntaryInsurancePath));
        voluntaryInsurance.click();

        //Проверить наличие заголовка – Добровольное медицинское страхование:
        String voluntaryInsuranceHeaderPath = "//h1[contains(text(), 'Добровольное медицинское страхование')]";
        WebElement voluntaryInsuranceHeader = driver.findElement(By.xpath(voluntaryInsuranceHeaderPath));
        Assert.assertEquals("Заголовок не совпадает", "Добровольное медицинское страхование", voluntaryInsuranceHeader.getText());

        //Нажать на кнопку – Отправить заявку:
        String sendRequestButtonPath = "//a[@class=\"action-item btn--basic\"]";

        WebElement sendRequestButton = driver.findElement(By.xpath(sendRequestButtonPath));
        Assert.assertTrue("Кнопка не отображена", sendRequestButton.isDisplayed());

        sendRequestButton.click();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        //Проверить видимость формы для заполнения:
        String userNameFieldPath = "//input[@name=\"userName\"]";
        WebElement userName = driver.findElement(By.xpath(userNameFieldPath));
        Assert.assertTrue("Поле ввода имени отсутствует",userName.isDisplayed());

        String userTelFieldPath = "//input[@name=\"userTel\"]";
        WebElement userTel = driver.findElement(By.xpath(userTelFieldPath));
        Assert.assertTrue("Поле ввода телефона отсутствует",userTel.isDisplayed());

        String userEmailFieldPath = "//input[@name=\"userEmail\"]";
        WebElement userEmail = driver.findElement(By.xpath(userEmailFieldPath));
        Assert.assertTrue("Поле ввода адреса почты отсутствует",userEmail.isDisplayed());

        String adressInputFieldPath = "//input[@class=\"vue-dadata__input\"]";
        WebElement addressInput = driver.findElement(By.xpath(adressInputFieldPath));
        Assert.assertTrue("Поле ввода адреса отсутствует",addressInput.isDisplayed());


        //Заполнить поля:

        //Имя, Фамилия, Отчество:
        String name = randomFioGenerator();
        userName.sendKeys(name);

        //Телефон:
        String phoneNumber = randomTelephoneNumberGenerator();
        userTel.sendKeys(phoneNumber);

        //Эл. почта – qwertyqwerty(пример заполнения почты):
        userEmail.sendKeys("qwertyqwerty");

        //Адрес:
        String address = randomAdressGenerator();
        addressInput.sendKeys(address);
        WebElement formWrapper = driver.findElement(By.xpath("//div[@class=\"form-wrapper\"]"));
        formWrapper.click();


        //Поставить галочку в чекбоксе "Я согласен на обработку":
        String checkBoxPath = "//input[@type=\"checkbox\"]/..";
        WebElement checkbox = driver.findElement(By.xpath(checkBoxPath));
        executor.executeScript("arguments[0].click();", checkbox);
        Assert.assertTrue("Чек-бокс неактивен", checkbox.getAttribute("class").contains("checkbox is-checked"));



        //Проверить, что все поля заполнены введенными значениями
        Assert.assertEquals("Введенное в поле имя не соответствует заданному", name, userName.getAttribute("value"));
        Assert.assertEquals("Введенный в поле номер не соответствует заданному", phoneNumber, userTel.getAttribute("value")
                .replaceAll("[\\p{Punct}&&[^+]]+", "").replaceAll("\\s", ""));
        Assert.assertEquals("Введенный в поле адрес почты не соответствует заданному", "qwertyqwerty", userEmail.getAttribute("value"));
        Assert.assertEquals("Введенный в поле адрес не соответствует заданному", address, addressInput.getAttribute("value"));


        //Нажать "Свяжитесь со мной"
        String submitButtonPath = "//button[contains(text(), 'Свяжитесь со мной')]";
        WebElement submitButton = driver.findElement(By.xpath(submitButtonPath));
        submitButton.submit();


        //Проверить, что у поля – Эл. почта присутствует сообщение об ошибке – "Введите корректный адрес электронной почты"
        String emailErrorMessagePath = "//span[contains(text(), 'Введите корректный адрес электронной почты')]";
        WebElement emailErrorMessage = driver.findElement(By.xpath(emailErrorMessagePath));
        Assert.assertEquals("Сообщение о неверно введенном адресе почты отсутствует",
                "Введите корректный адрес электронной почты", emailErrorMessage.getText());
        //Проверить, что кнопка "Свяжитесь со мной" неактивна
        String disabledSubmitButtonPath = "//button[@class=\"form__button-submit btn--basic btn--disabled\"]";
        WebElement disabledSubmitButton = driver.findElement(By.xpath(disabledSubmitButtonPath));
        Assert.assertFalse("Кнопка все еще активна", disabledSubmitButton.isEnabled());


        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @After
    public void after() {
        driver.quit();
    }

    public String randomAdressGenerator() {
        StringBuilder str = new StringBuilder();
        List<String> street = Arrays.asList("проезд 10-й Марьиной Рощи, д 25", "ул Забелина, д 1", "Савёловский проезд, д 8 стр 1", "Щёлковский проезд, д 1А",
                "Оболенский пер, д 3", "Зелёный пр-кт, д 2", "ул Косинская, д 12Б");
        int random = (int) ((Math.random()*street.size())-1);
        str.append("г Москва, ").append(street.get(random));
        return str.toString();
    }
    public String randomTelephoneNumberGenerator() {
        String numbers = "0123456789";
        int length = 9;
        StringBuilder phoneNumber = new StringBuilder();
        phoneNumber.append("+79");
        for (int i = 0; i < length; i++) {
            int random = (int) ((Math.random()*length)-1);
            phoneNumber.append(numbers.charAt(random));
        }
        return phoneNumber.toString();
    }
    public String randomFioGenerator() {
        StringBuilder fio = new StringBuilder();
        List<String> lastName = Arrays.asList("Старостин", "Корнеев", "Малинин", "Игнатьев", "Глушков", "Воронин",
                "Александров", "Зайцев", "Кузнецов", "Романов");
        List<String> firstName = Arrays.asList("Матвей", "Сергей", "Захар", "Михаил", "Александр",
                "Иван", "Дмитрий", "Максим", "Адам", "Тимофей");
        List<String> middleName = Arrays.asList("Денисович", "Тимофеевич", "Сергеевич", "Георгиевич", "Кириллович", "Михайлович",
                "Александрович", "Владиславович", "Маратович", "Степанович");
        int random = (int) ((Math.random()* firstName.size())-1);
        fio.append(lastName.get(random)).append(" ").append(firstName.get(random)).append(" ").append(middleName.get(random));
        return fio.toString();
    }
}

