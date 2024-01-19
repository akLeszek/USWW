package adrianles.usww;

import adrianles.usww.utils.ResourceFileLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserJsonTest {
    @Autowired
    private JacksonTester<User> json;
    private ClassLoader classLoader = this.getClass().getClassLoader();

    @Test
    public void userJsonSerializationTest() throws IOException {
        User user = new User(99L, "test", "12345");
        File userJsonTest = ResourceFileLoader.getJsonFile("user.json", classLoader);

        assertThat(json.write(user)).isStrictlyEqualToJson(userJsonTest);
        assertThat(json.write(user)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(user)).extractingJsonPathNumberValue("@.id")
                .isEqualTo(99);
        assertThat(json.write(user)).hasJsonPathStringValue("@.login");
        assertThat(json.write(user)).extractingJsonPathStringValue("@.login")
                .isEqualTo("test");
        assertThat(json.write(user)).hasJsonPathStringValue("@.password");
        assertThat(json.write(user)).extractingJsonPathStringValue("@.password")
                .isEqualTo("12345");
    }

    @Test
    public void userJsonDeserializationTest() throws IOException {
        String userJsonTest = ResourceFileLoader.getJsonFileAsString("user.json", classLoader);
        assertThat(json.parse(userJsonTest)).isEqualTo(new User(99L, "test", "12345"));
        assertThat(json.parseObject(userJsonTest).id()).isEqualTo(99);
        assertThat(json.parseObject(userJsonTest).login()).isEqualTo("test");
        assertThat(json.parseObject(userJsonTest).password()).isEqualTo("12345");
    }

}
