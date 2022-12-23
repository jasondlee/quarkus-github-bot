package io.quarkus.bot.it;

import static io.quarkiverse.githubapp.testing.GitHubAppTesting.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHEvent;

import io.quarkiverse.githubapp.testing.GitHubAppTest;
import io.quarkus.bot.CheckIssueEditorialRules;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@GitHubAppTest
public class CheckIssueEditorialRulesTest {
    @Test
    void validZulipLinkConfirmation() throws IOException {
        given().github(mocks -> mocks.configFile("quarkus-github-bot.yml").fromString("features: [ ALL ]\n"))
                .when().payloadFromClasspath("/issue-opened-zulip.json")
                .event(GHEvent.ISSUES)
                .then().github(mocks -> {
                    verify(mocks.issue(942074921))
                            .comment(CheckIssueEditorialRules.ZULIP_WARNING);
                    verifyNoMoreInteractions(mocks.ghObjects());
                });

    }

    @Test
    void testLabelBackportWarningConfirmation() throws IOException {
        String warningMsg = String.format(CheckIssueEditorialRules.LABEL_BACKPORT_WARNING, "triage/backport-whatever");
        StringBuilder expectedComment = new StringBuilder("@test-github-user " + warningMsg);
        expectedComment.append("\n> This message is automatically generated by a bot.");

        given().github(mocks -> mocks.configFile("quarkus-github-bot.yml").fromString("features: [ ALL ]\n"))
                .when().payloadFromString(getSampleIssueLabelTriageBackportPayload())
                .event(GHEvent.ISSUES)
                .then().github(mocks -> {
                    verify(mocks.issue(1234567890))
                            .comment(expectedComment.toString());
                    verifyNoMoreInteractions(mocks.ghObjects());
                });

    }

    private static String getSampleIssueLabelTriageBackportPayload() {
        return """
                {
                    "action": "labeled",
                    "issue": {
                      "id": 1234567890,
                      "number": 123,
                      "labels": [
                        {
                          "name": "triage/backport-whatever"
                        }
                      ]
                    },
                    "label": {
                      "name": "triage/backport-whatever"
                    },
                    "repository": {

                    },
                    "sender": {
                      "login": "test-github-user"
                    }
                  }
                    """;
    }
}
