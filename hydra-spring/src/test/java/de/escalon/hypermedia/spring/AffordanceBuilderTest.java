/*
 * Copyright (c) 2014. Escalon System-Entwicklung, Dietrich Schulten
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package de.escalon.hypermedia.spring;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.Assert.*;

public class AffordanceBuilderTest {

    private MockHttpServletRequest request;

    @Before
    public void setUp() {
        request = MockMvcRequestBuilders.get("http://example.com/api/gadgets")
                .buildRequest(new MockServletContext());
        final RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    public static class Thing {

    }

    public static class DummyController {

        @RequestMapping("/things")
        public ResponseEntity createThing(Thing thing) {
            return new ResponseEntity(HttpStatus.CREATED);
        }

    }

    @Test
    public void testWithSingleRel() throws Exception {
        final Affordance affordance = AffordanceBuilder.linkTo(AffordanceBuilder.methodOn(DummyController.class)
                .createThing(new Thing()))
                .build("next");
        assertEquals("Link: <http://example.com/things>; rel=\"next\"", affordance.toLinkHeader());
    }

    @Test
    public void testWithTitle() {
        final Affordance affordance = AffordanceBuilder.linkTo(AffordanceBuilder.methodOn(DummyController.class)
                .createThing(new Thing()))
                .withTitle("my-title")
                .build("next");
        assertEquals("Link: <http://example.com/things>; rel=\"next\"; title=\"my-title\"",
                affordance.toLinkHeader());
    }

    @Test
    public void testWithTitleStar() {
        final Affordance affordance = AffordanceBuilder.linkTo(AffordanceBuilder.methodOn(DummyController.class)
                .createThing(new Thing()))
                .withTitleStar("UTF-8'de'n%c3%a4chstes%20Kapitel")
                .build("next");
        assertEquals("Link: <http://example.com/things>; rel=\"next\"; title*=\"UTF-8'de'n%c3%a4chstes%20Kapitel\"",
                affordance.toLinkHeader());
    }

    @Test
    public void testWithAnchor() {
        final Affordance affordance = AffordanceBuilder.linkTo(AffordanceBuilder.methodOn(DummyController.class)
                .createThing(new Thing()))
                .withAnchor("http://api.example.com/api")
                .build("next");
        assertEquals("Link: <http://example.com/things>; rel=\"next\"; anchor=\"http://api.example.com/api\"",
                affordance.toLinkHeader());
    }

    @Test
    public void testWithType() {
        final Affordance affordance = AffordanceBuilder.linkTo(AffordanceBuilder.methodOn(DummyController.class)
                .createThing(new Thing()))
                .withType("application/pdf")
                .build("next");
        assertEquals("Link: <http://example.com/things>; rel=\"next\"; type=\"application/pdf\"",
                affordance.toLinkHeader());
    }

    @Test
    public void testWithMedia() {
        final Affordance affordance = AffordanceBuilder.linkTo(AffordanceBuilder.methodOn(DummyController.class)
                .createThing(new Thing()))
                .withMedia("qhd")
                .build("next");
        assertEquals("Link: <http://example.com/things>; rel=\"next\"; media=\"qhd\"",
                affordance.toLinkHeader());
    }

    @Test
    public void testWithHreflang() {
        final Affordance affordance = AffordanceBuilder.linkTo(AffordanceBuilder.methodOn(DummyController.class)
                .createThing(new Thing()))
                .withHreflang("en-us")
                .withHreflang("de")
                .build("next");
        assertEquals("Link: <http://example.com/things>; rel=\"next\"; hreflang=\"en-us\"; hreflang=\"de\"",
                affordance.toLinkHeader());
    }

    @Test
    public void testWithLinkParam() {
        final Affordance affordance = AffordanceBuilder.linkTo(AffordanceBuilder.methodOn(DummyController.class)
                .createThing(new Thing()))
                .withLinkParam("param1", "foo")
                .withLinkParam("param1", "bar")
                .withLinkParam("param2", "baz")
                .build("next");
        assertEquals("Link: <http://example.com/things>; rel=\"next\"; param1=\"foo\"; param1=\"bar\"; param2=\"baz\"",
                affordance.toLinkHeader());
    }

    @Test
    public void testBuild() throws Exception {
        final Affordance affordance = AffordanceBuilder.linkTo(AffordanceBuilder.methodOn(DummyController.class)
                .createThing(new Thing()))
                .build("next", "thing");
        assertEquals("Link: <http://example.com/things>; rel=\"next thing\"", affordance.toLinkHeader());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectsEmptyRel() throws Exception {
        final Affordance affordance = AffordanceBuilder.linkTo(AffordanceBuilder.methodOn(DummyController.class)
                .createThing(new Thing()))
                .build("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectsEmptyRels() throws Exception {
        final Affordance affordance = AffordanceBuilder.linkTo(AffordanceBuilder.methodOn(DummyController.class)
                .createThing(new Thing()))
                .build(new String[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectsMissingRel() throws Exception {
        final Affordance affordance = AffordanceBuilder.linkTo(AffordanceBuilder.methodOn(DummyController.class)
                .createThing(new Thing()))
                .build();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testRejectsNullRel() throws Exception {
        final Affordance affordance = AffordanceBuilder.linkTo(AffordanceBuilder.methodOn(DummyController.class)
                .createThing(new Thing()))
                .build(null);
    }

}