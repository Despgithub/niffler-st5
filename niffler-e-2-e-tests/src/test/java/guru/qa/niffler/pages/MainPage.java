package guru.qa.niffler.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage {

    private final ElementsCollection spendingRows = $$x("//table[@class='table spendings-table']/tbody/tr");
    private final SelenideElement deleteSelectedButton = $x(".//button[.='Delete selected']");


    public SelenideElement findSpendingRow(String description) {
        return spendingRows.find(text(description));
    }

    public MainPage chooseSpending(SelenideElement spendings) {
        spendings.$x("td").scrollTo().click();
        return this;
    }

    public MainPage clickDeleteSelectedButton() {
        deleteSelectedButton.click();
        return this;
    }

    public void checkSpendings(int expectedSize) {
        spendingRows.shouldHave(size(expectedSize));
    }

}
