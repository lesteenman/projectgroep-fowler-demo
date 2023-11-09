package org.example;

import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertEquals;

public class HelloLambdaTest {

    @Test
    public void shouldReturnHelloJurForParamJur() {
        var sut = new HelloLambda();
        assertEquals("Hello, Jur!", sut.handleRequest("Jur"));
    }
}