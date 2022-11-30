package com.artem.junit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;

import com.artem.junit.TestBase;
import com.artem.junit.dao.UserDao;
import com.artem.junit.dto.User;
import com.artem.junit.extension.ConditionalExtension;
import com.artem.junit.extension.PostProcessingExtension;
import com.artem.junit.extension.UserServiceParamResolver;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("fast")
@Tag("user")
@TestInstance(Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.MethodName.class)
@ExtendWith({
    UserServiceParamResolver.class,
    MockitoExtension.class,
//    GlobalExtension.class,
    PostProcessingExtension.class,
    ConditionalExtension.class
//    ThrowableExtension.class
})
public class UserServiceTest extends TestBase {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");

    @Captor
    private ArgumentCaptor<Integer> argumentCaptor;
    @Mock(lenient = true)
    private UserDao userDao;
    @InjectMocks
    private UserService userService;

    UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }

    @BeforeAll
    static void init() {
        System.out.println("After all");
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this);
//        userService.userDao = Mockito.mock()
//        lenient().when(userDao.delete(IVAN.getId())).thenReturn(true);
//        Mockito.mockStatic()
//        lenient().when(userDao.delete(IVAN.getId())).thenReturn(true);

        doReturn(true).when(userDao).delete(IVAN.getId());
//        Mockito.mock(UserDao.class, withSettings().)
//        Mockito.mock(UserDao.class, withSettings().lenient())
//        this.userDao = Mockito.spy(new UserDao());
//        this.userService = new UserService(userDao);
    }

    @Test
    void throwExceptionIfDatabaseIsNotAvailable() {
        doThrow(RuntimeException.class).when(userDao).delete(IVAN.getId());

        assertThrows(RuntimeException.class, () -> userService.delete(IVAN.getId()));
    }

    @Test
    void shouldDeleteExistedUser() {
        var id = IVAN.getId();

        userService.add(IVAN);
        doReturn(true).when(userDao).delete(id);
//        Mockito.doReturn(true).when(userDao).delete(Mockito.any());

//        Mockito.when(userDao.delete(IVAN.getId()))
//            .thenReturn(true)
//            .thenReturn(false);

        var deleteResult = userService.delete(id);
        System.out.println(userService.delete(id));
        System.out.println(userService.delete(id));

        Mockito.verify(userDao, Mockito.times(3)).delete(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).isEqualTo(IVAN.getId());
        assertThat(deleteResult).isTrue();
    }

    @Test
    @DisplayName("users will be empty if users no added")
    void usersEmptyIfNoUserAdded() throws IOException {
//        if (true) {
//            throw new RuntimeException();
//        }

        System.out.println("Test 1: " + this);
        var users = userService.getAll();
        assertTrue(users.isEmpty());
    }

    @Test
    void userSizeIfUserAdded() {
        System.out.println("Test 2: " + this);
        userService.add(IVAN);
        userService.add(PETR);

        var users = userService.getAll();

        assertThat(users).hasSize(2);
//        assertEquals(2, users.size());
    }

    @Test
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);

        Map<Integer, User> users = userService.getAllConvertedById();

        assertAll(
            () -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
            () -> assertThat(users).containsValues(IVAN, PETR)
        );
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each: " + this);
    }

    @AfterAll
    static void closeConnectionPool() {
        System.out.println("After all");
    }

    @Nested
    @Tag("login")
    class LoginTest {

        @Test
        @Disabled("fluky")
        void loginFailIfPasswordIsNotCorrect() {
            userService.add(IVAN);

            Optional<User> maybeUser = userService.login(IVAN.getUsername(), "dummy");

            assertTrue(maybeUser.isEmpty());
        }

        @Test
        @Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
        void checkLoginFunctionalityPerformance() {
            System.out.println(Thread.currentThread().getName());
            var result = assertTimeoutPreemptively(Duration.ofMillis(200L), () -> {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(300L);
                return userService.login(IVAN.getUsername(), IVAN.getPassword());
            });
        }

        //        @Test
        @RepeatedTest(value = 4, name = RepeatedTest.LONG_DISPLAY_NAME)
        void loginFailIfUserDoesNotExist(RepetitionInfo repetitionInfo) {
            userService.add(IVAN);

            Optional<User> maybeUser = userService.login("dummy", "dummy");

            assertTrue(maybeUser.isEmpty());
        }

        @Test
        void throwExceptionIfUsernameOrPasswordIsNull() {
            assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy")),
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
            );
        }

        @ParameterizedTest(name = "{arguments} test")
//        @ArgumentsSource()
//        @NullSource
//        @EmptySource
//        @NullAndEmptySource
//        @ValueSource(strings = {
//            "Ivan", "Petr"
//        })
        @MethodSource("com.artem.junit.service.UserServiceTest#getArgumentsForLoginTest")
//        @CsvFileSource(resources = "/login-test-data.csv", numLinesToSkip = 1)
//        @CsvSource({
//            "Ivan,123",
//            "Petr,111",
//        })
        @DisplayName("login param test")
        void loginParameterizedTest(String username, String password, Optional<User> user) {
            userService.add(IVAN, PETR);

            var maybeUser = userService.login(username, password);
            assertThat(maybeUser).isEqualTo(user);

        }

        @Test
        void loginSuccessIfUserExists() {
            userService.add(IVAN);

            Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

            assertThat(maybeUser).isPresent();
            maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));
//        assertTrue(maybeUser.isPresent());
//        maybeUser.ifPresent(user -> assertEquals(IVAN, user));
        }
    }

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
            Arguments.of("Ivan", "123", Optional.of(IVAN)),
            Arguments.of("Petr", "111", Optional.of(PETR)),
            Arguments.of("Petr", "dummy", Optional.empty()),
            Arguments.of("dummy", "123", Optional.empty())
        );
    }
}
