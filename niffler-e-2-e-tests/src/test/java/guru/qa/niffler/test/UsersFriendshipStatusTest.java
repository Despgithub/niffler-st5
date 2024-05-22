package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.UserQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pages.LoginPage;
import guru.qa.niffler.pages.MainPage;
import guru.qa.niffler.pages.PeoplePage;
import guru.qa.niffler.pages.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.chrome.ChromeOptions;

import static guru.qa.niffler.jupiter.annotation.User.Selector.*;

@ExtendWith(UserQueueExtension.class)
public class UsersFriendshipStatusTest {

    static {
        Configuration.browserSize = "1920x1080";
        Configuration.browserCapabilities = new ChromeOptions()
                .addArguments("--incognito");
    }

    private final WelcomePage welcomePage = new WelcomePage();
    private final LoginPage loginPage = new LoginPage();
    private final MainPage mainPage = new MainPage();
    private final PeoplePage peoplePage = new PeoplePage();

    @BeforeEach
    void openBrowser() {
        Selenide.open("http://127.0.0.1:3000/main");
        welcomePage.clickLoginButton();
    }

    @Test
    void userShouldHavePendingInvitationStatus(@User(INVITATION_SEND) UserJson userForTest, @User(INVITATION_RECEIVED) UserJson anotherUserForTest) {
        loginPage.login(userForTest.username(), userForTest.testData().password());
        mainPage.openPeoplePage();
        peoplePage.checkSendInvitation(anotherUserForTest.username());
    }

    @Test
    void userShouldHaveFriendshipStatus(@User(WITH_FRIENDS) UserJson userForTest, @User(INVITATION_SEND) UserJson anotherUserForTest) {
        loginPage.login(userForTest.username(), userForTest.testData().password());
        mainPage.openPeoplePage();
        peoplePage.checkFriendship(anotherUserForTest.username());
    }

    @Test
    void userShouldHaveReceivedInvitation(@User(INVITATION_RECEIVED) UserJson userForTest, @User(INVITATION_SEND) UserJson anotherUserForTest) {
        loginPage.login(userForTest.username(), userForTest.testData().password());
        mainPage.openPeoplePage();
        peoplePage.checkReceiveInvitation(anotherUserForTest.username());
    }

}
