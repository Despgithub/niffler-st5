package guru.qa.niffler.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$x;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PeoplePage {

    private final ElementsCollection peopleRows = $$x("//table[@class='table abstract-table']/tbody/tr");
    private final String submitInvitation = "//div[@data-tooltip-id='submit-invitation']";

    public void checkSendInvitatation(String userName) {
        SelenideElement td = findUserByName(userName);
        td.shouldHave(text("Pending invitation"));
    }

    public void checkFriendship(String userName) {
        SelenideElement td = findUserByName(userName);
        td.shouldHave(text("You are friends"));
    }

    public void checkReceiveInvitatation(String userName) {
        SelenideElement td = findUserByName(userName);
        assertTrue(td.findElement(By.xpath(submitInvitation)).isDisplayed());
    }

    private SelenideElement findUserByName(String userName) {
        return peopleRows.find(text(userName)).$$x("td").last();
    }

}
