package com.github.ansonliao.selenium.factory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.util.Strings;

import java.net.MalformedURLException;
import java.net.URL;

import static com.github.ansonliao.selenium.utils.PlatformUtils.getPlatform;
import static com.github.ansonliao.selenium.utils.StringUtils.removeQuoteMark;
import static com.github.ansonliao.selenium.utils.config.SEConfigs.getConfigInstance;
import static org.openqa.selenium.remote.BrowserType.FIREFOX;

public class FirefoxFactory extends DriverManager {

    private static final Logger logger =
            LoggerFactory.getLogger(FirefoxFactory.class);
    private FirefoxBinary binary = new FirefoxBinary();
    private FirefoxOptions options = new FirefoxOptions();
    private static FirefoxFactory instance = new FirefoxFactory();

    private FirefoxFactory() {
        super();
    }

    public synchronized static FirefoxFactory getInstance() {
        return instance;
    }

    @Override
    public WebDriver getDriver() {
        if (isHeadless) {
            binary.addCommandLineOptions("--headless");
            options.setBinary(binary);
        }
        if (isIncognito) {
            options.addArguments("--private");
        }

        driver = Strings.isNullOrEmpty(SELENIUM_HUB_URL)
                ? new FirefoxDriver(options)
                : buildRemoteWebDriver();
        return driver;
    }

    @Override
    protected WebDriver buildRemoteWebDriver() {
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, capabilities);
        capabilities.setCapability(CapabilityType.BROWSER_NAME, FIREFOX);
        capabilities.setCapability(CapabilityType.PLATFORM, getPlatform());
        capabilities.setCapability("tz", getTimezone());
        RemoteWebDriver remoteWebDriver = null;
        try {
            logger.info("Create RemoteWebDriver instance with Selenium Hub URL: {}", SELENIUM_HUB_URL);
            remoteWebDriver = new RemoteWebDriver(new URL(SELENIUM_HUB_URL), capabilities);
        } catch (MalformedURLException e) {
            logger.error("Malformed URL found: {}", SELENIUM_HUB_URL);
            e.printStackTrace();
        }
        return remoteWebDriver;
    }

    @Override
    public String getExportParameterKey() {
        return removeQuoteMark(getConfigInstance().firefoxDriverProperty());
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

}
