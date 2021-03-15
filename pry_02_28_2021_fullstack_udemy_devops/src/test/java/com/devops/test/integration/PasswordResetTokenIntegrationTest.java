package com.devops.test.integration;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.devops.backend.persistence.domain.backend.PasswordResetToken;
import com.devops.backend.persistence.domain.backend.User;
import com.devops.backend.persistence.repositories.PasswordResetTokenRepository;

@SpringBootTest
public class PasswordResetTokenIntegrationTest extends AbstractIntegrationTest {

    @Value("${token.expiration.length.minutes}")
    private int expirationTimeInMinutes;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Rule public TestName testName = new TestName();

    @Before
    public void init() {
        Assert.assertFalse(expirationTimeInMinutes == 0);
    }

    // Verificar si la lógica de la fecha de expiración funciona
    @Test
    public void testTokenExpirationLength() throws Exception {

        User user = createUser(testName);
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());


        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        String token = UUID.randomUUID().toString();

        LocalDateTime expectedTime = now.plusMinutes(expirationTimeInMinutes);

        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);

        LocalDateTime actualTime = passwordResetToken.getExpiryDate();
        Assert.assertNotNull(actualTime);
        Assert.assertEquals(expectedTime, actualTime);
    }

    // Para verificar que se puede hallar un registro por su token-value
    @Test
    public void testFindTokenByTokenValue() throws Exception {

        User user = createUser(testName);
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        createPasswordResetToken(token, user, now);

        PasswordResetToken retrievedPasswordResetToken = passwordResetTokenRepository.findByToken(token);
        Assert.assertNotNull(retrievedPasswordResetToken);
        Assert.assertNotNull(retrievedPasswordResetToken.getId());
        Assert.assertNotNull(retrievedPasswordResetToken.getUser());
    }

    // Para verificar que se puede borrar un token por su PK
    @Test
    public void testDeleteToken() throws Exception {

        User user = createUser(testName);
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);
        long tokenId = passwordResetToken.getId();
        passwordResetTokenRepository.deleteById(tokenId);

        PasswordResetToken shouldNotExistToken = passwordResetTokenRepository.findById(tokenId).orElse(null);
        Assert.assertNull(shouldNotExistToken);
    }

    // Para verificar que si se borra un User, sus tokens también se borrarán
    @Test
    public void testCascadeDeleteFromUserEntity() throws Exception {

        User user = createUser(testName);
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);
        passwordResetToken.getId();

        userRepository.deleteById(user.getId());

        Set<PasswordResetToken> shouldBeEmpty = passwordResetTokenRepository.findAllByUserId(user.getId());
        Assert.assertTrue(shouldBeEmpty.isEmpty());
    }

    @Test
    public void testMultipleTokensAreReturnedWhenQueringByUserId() throws Exception {

        User user = createUser(testName);
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        String token1 = UUID.randomUUID().toString();
        String token2 = UUID.randomUUID().toString();
        String token3 = UUID.randomUUID().toString();

        Set<PasswordResetToken> tokens = new HashSet<>();
        tokens.add(createPasswordResetToken(token1, user, now));
        tokens.add(createPasswordResetToken(token2, user, now));
        tokens.add(createPasswordResetToken(token3, user, now));

        passwordResetTokenRepository.saveAll(tokens);

        User founduser = userRepository.findById(user.getId()).orElse(null);

        Set<PasswordResetToken> actualTokens = passwordResetTokenRepository.findAllByUserId(founduser.getId());
        Assert.assertTrue(actualTokens.size() == tokens.size());
        List<String> tokensAsList = tokens.stream().map(prt -> prt.getToken()).collect(Collectors.toList());
        List<String> actualTokensAsList = actualTokens.stream().map(prt -> prt.getToken()).collect(Collectors.toList());
        Assert.assertEquals(tokensAsList, actualTokensAsList);
    }


    //------------------> Private methods

    private PasswordResetToken createPasswordResetToken(String token, User user, LocalDateTime now) {


        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, now, expirationTimeInMinutes);
        passwordResetTokenRepository.save(passwordResetToken);
        Assert.assertNotNull(passwordResetToken.getId());
        return passwordResetToken;
    }

}