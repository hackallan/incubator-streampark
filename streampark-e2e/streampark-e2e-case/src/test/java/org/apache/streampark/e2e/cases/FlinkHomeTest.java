/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.streampark.e2e.cases;

import org.apache.streampark.e2e.core.StreamPark;
import org.apache.streampark.e2e.pages.LoginPage;
import org.apache.streampark.e2e.pages.flink.ApacheFlinkPage;
import org.apache.streampark.e2e.pages.flink.FlinkHomePage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import static org.assertj.core.api.Assertions.assertThat;

@StreamPark(composeFiles = "docker/flink-1.18-on-yarn/docker-compose.yaml")
public class FlinkHomeTest {

    public static RemoteWebDriver browser;

    private static final String flinkName = "flink-1.18.1";

    private static final String flinkHome = "/flink-1.18.1";

    private static final String flinkDescription = "description test";

    private static final String newFlinkHome = "flink_1.18.1";

    @BeforeAll
    public static void setup() {
        new LoginPage(browser)
            .login()
            .goToNav(ApacheFlinkPage.class)
            .goToTab(FlinkHomePage.class);
    }

    @Test
    @Order(1)
    void testCreateFlinkHome() {
        final FlinkHomePage flinkHomePage = new FlinkHomePage(browser);
        flinkHomePage.createFlinkHome(flinkName, flinkHome, flinkDescription);

        Awaitility.await()
            .untilAsserted(
                () -> assertThat(flinkHomePage.flinkHomeList)
                    .as("Flink Home list should contain newly-created flink home")
                    .extracting(WebElement::getText)
                    .anyMatch(it -> it.contains(flinkName)));
    }

    @Test
    @Order(2)
    void testEditFlinkHome() {
        final FlinkHomePage flinkHomePage = new FlinkHomePage(browser);
        flinkHomePage.editFlinkHome(flinkName, newFlinkHome);

        Awaitility.await()
            .untilAsserted(
                () -> assertThat(flinkHomePage.flinkHomeList)
                    .as("Flink Home list should contain edited flink home")
                    .extracting(WebElement::getText)
                    .anyMatch(it -> it.contains(newFlinkHome)));
    }

    @Test
    @Order(3)
    void testDeleteFlinkHome() {
        final FlinkHomePage flinkHomePage = new FlinkHomePage(browser);
        flinkHomePage.deleteFlinkHome(newFlinkHome);

        Awaitility.await()
            .untilAsserted(
                () -> {
                    browser.navigate().refresh();

                    assertThat(flinkHomePage.flinkHomeList)
                        .noneMatch(it -> it.getText().contains(newFlinkHome));
                });
    }
}
