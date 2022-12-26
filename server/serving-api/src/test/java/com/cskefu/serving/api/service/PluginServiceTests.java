package com.cskefu.serving.api.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cskefu.serving.api.config.PluginsConfigurer;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = PluginService.class)
class PluginServiceTests {

	@MockBean
	private SpringAwareBundleListener springAwareBundleListener;
	@MockBean
	private PluginsConfigurer pluginsConfigurer;
	@Autowired
	private PluginService cut;

	@Test
	@DisplayName(
			"when BundleService is initialized, then the Felix framework is created using the Spring ApplicationReadyEvent")
	void testFelixFrameworkCreation() {
		assertAll(
				() -> assertNotNull(cut),
				() -> assertNotNull(cut.getFramework())
		);
	}

	@Test
	@DisplayName("When find package is called with a specific package, then subpackages are provided too")
	void testExtraPackageConfiguration() {
		String result = cut.findPackageNamesStartingWith(List.of("com.mornati.sample"));
		assertAll(
				() -> assertNotNull(result),
				() -> assertTrue(result.contains("com.mornati.sample.service")),
				() -> assertTrue(result.contains("com.mornati.sample.commons.plugins.dto"))
		);
	}
}
