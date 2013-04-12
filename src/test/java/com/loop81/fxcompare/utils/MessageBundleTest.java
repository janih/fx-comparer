package com.loop81.fxcompare.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.fest.assertions.api.Assertions;
import org.junit.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test for {@link MessageBundle}.
 * 
 * @author Allitico
 */
@Test
public class MessageBundleTest {

	@BeforeClass
	public static void init() {
		MessageBundle.initialize(ResourceBundle.getBundle("text.messages"));
	}
	
	public void shouldBePossibleToGetASimpleMessage() {
		Assertions.assertThat(MessageBundle.getString("property.simple")).isEqualTo("Hello!");
	}
	
	public void shouldBePossibleToGetAStringWithAParameter() {
		Assertions.assertThat(MessageBundle.getString("property.one", "Fish")).isEqualTo("Hello Fish!");
	}
	
	public void shouldBePossibleToGetAStringWithManyParameters() {
		Assertions.assertThat(MessageBundle.getString("property.many", "42", "==", "42")).isEqualTo("42 == 42");
	}
	
	@Test(expectedExceptions = { MissingResourceException.class} )
	public void shouldGetAExceptionWithDescriptionForTheKeyWhenNotFound() {
		MessageBundle.getString("will.not.be.found");
	}
}
