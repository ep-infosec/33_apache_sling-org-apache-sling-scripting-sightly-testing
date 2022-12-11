/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package org.apache.sling.scripting.sightly.it;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.sling.testing.clients.util.FormEntityBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import io.sightly.tck.html.HTMLExtractor;
import io.sightly.tck.http.Client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class SlingSpecificsSightlyIT {

    private static Client client;
    private static String launchpadURL;
    private static final String SLING_USE = "/content/sightly/use.html";
    private static final String SLING_JAVA_USE_NPE = "/content/sightly/use.javaerror.html";
    private static final String SLING_JAVA_USE_INTERFACE = "/content/sightly/use.interface.html";
    private static final String SLING_JAVA_USE_ABSTRACT = "/content/sightly/use.abstractClass.html";
    private static final String SLING_JAVA_USE_SLING_MODEL_ERROR = "/content/sightly/use.slingmodel-error.html";
    private static final String SLING_RESOURCE = "/content/sightly/resource.html";
    private static final String SLING_RESOURCE_ACTUAL = "/content/sightly/actualresource.html";
    private static final String SLING_TEMPLATE = "/content/sightly/template.html";
    private static final String SLING_TEMPLATE_BAD_IDENTIFIER = "/content/sightly/template.bad-id.html";
    private static final String SLING_JS_USE = "/content/sightly/use.jsuse.html";
    private static final String SLING_JS_DEPENDENCY_RESOLUTION = "/content/sightly/use-sibling-dependency-resolution.html";
    private static final String SLING_USE_INHERITANCE_WITHOVERLAY = "/content/sightly/useinheritance.html";
    private static final String SLING_USE_INHERITANCE_WITHOUTOVERLAY = "/content/sightly/useinheritance.notoverlaid.html";
    private static final String SLING_USE_INHERITANCE_AND_OVERLAY = "/content/sightly/inherit.html";
    private static final String SLING_JAVA_USE_POJO_UPDATE = "/content/sightly/use.repopojo.html";
    private static final String SLING_ATTRIBUTE_QUOTES = "/content/sightly/attributequotes.html";
    private static final String SLING_CRLF = "/content/sightly/crlf";
    private static final String SLING_CRLF_NOPKG = SLING_CRLF + ".nopkg.html";
    private static final String SLING_CRLF_PKG = SLING_CRLF + ".pkg.html";
    private static final String SLING_CRLF_WRONGPKG = SLING_CRLF + ".wrongpkg.html";
    private static final String SLING_FORMAT = "/content/sightly/format.html";
    private static final String SLING_SCRIPT_UPDATE = "/content/sightly/update.html";
    private static final String SLING_REQUEST_ATTRIBUTES = "/content/sightly/requestattributes.html";
    private static final String SLING_REQUEST_ATTRIBUTES_INCLUDE = "/content/sightly/requestattributes.include.html";
    private static final String SLING_RESOURCE_USE = "/content/sightly/use.resource.html";
    private static final String SLING_I18N = "/content/sightly/i18n";
    private static final String SLING_XSS = "/content/sightly/xss.html";
    private static final String TCK_XSS = "/sightlytck/exprlang/xss.html";
    private static final String WHITESPACE = "/content/sightly/whitespace.html";
    private static final String SYNTHETIC_RESOURCE = "/content/sightly/synthetic-resource.html";
    private static final String PRECOMPILED = "/sightly-testing/precompiled.html";

    @BeforeClass
    public static void init() {
        launchpadURL = System.getProperty("launchpad.http.server.url");
        client = new Client();
    }

    @Test
    public void testSlingModelsUseAPI() {
        String url = launchpadURL + SLING_USE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("SUCCESS", HTMLExtractor.innerHTML(url, pageContent, "#reqmodel"));
        assertEquals("SUCCESS", HTMLExtractor.innerHTML(url, pageContent, "#reqmodel-reqarg"));
        assertEquals("nt:unstructured", HTMLExtractor.innerHTML(url, pageContent, "#reqmodel-bindings"));
        assertEquals("SUCCESS", HTMLExtractor.innerHTML(url, pageContent, "#resmodel"));
        
    }
    
    @Test
    public void testAdaptablesUseAPI() {
        String url = launchpadURL + SLING_USE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("SUCCESS", HTMLExtractor.innerHTML(url, pageContent, "#resadapt"));
        assertEquals("SUCCESS", HTMLExtractor.innerHTML(url, pageContent, "#reqadapt"));
        assertEquals("SUCCESS", HTMLExtractor.innerHTML(url, pageContent, "#rradapt"));
    }

    @Test
    public void testUseAPIWithOSGIService() {
        String url = launchpadURL + SLING_USE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("Hello World!", HTMLExtractor.innerHTML(url, pageContent, "#osgi"));
    }

    @Test
    public void testEnumConstantAsString() {
        String url = launchpadURL + SLING_USE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("ENUM_CONSTANT", HTMLExtractor.innerHTML(url, pageContent, "#test-enum"));
    }

    /**
     * SLING-10677
     */
    @Test
    public void testEnumValueAsString() {
        String url = launchpadURL + SLING_USE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("ENUM_CONSTANT", HTMLExtractor.innerHTML(url, pageContent, "#test-enum-value"));
    }

    /**
     * SLING-10677
     */
    @Test
    public void testEnumStaticFieldAsString() {
        String url = launchpadURL + SLING_USE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("static field1", HTMLExtractor.innerHTML(url, pageContent, "#test-enum-staticfield"));
    }

    /**
     * SLING-10677
     */
    @Test
    public void testEnumStaticMethodAsString() {
        String url = launchpadURL + SLING_USE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("something", HTMLExtractor.innerHTML(url, pageContent, "#test-enum-staticmethod"));
    }

    /**
     * SLING-10677
     */
    @Test
    public void testEnumValuesAsString() {
        String url = launchpadURL + SLING_USE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("ENUM_CONSTANT", HTMLExtractor.innerHTML(url, pageContent, "#test-enum-value2-0"));
        assertEquals("ENUM_CONSTANT2", HTMLExtractor.innerHTML(url, pageContent, "#test-enum-value2-1"));
    }

    @Test
    public void testErroneousUseObject() {
        String url = launchpadURL + SLING_JAVA_USE_NPE;
        String pageContent = client.getStringContent(url, 500);
        assertTrue(pageContent.contains("java.lang.NullPointerException"));
    }

    @Test
    public void testInterfaceUset() {
        String url = launchpadURL + SLING_JAVA_USE_INTERFACE;
        String pageContent = client.getStringContent(url, 500);
        assertTrue(pageContent.contains(
                "org.apache.sling.scripting.sightly.testing.adaptable.NonImplementedInterface represents an interface or an abstract class which cannot be instantiated"));
    }

    @Test
    public void testAbstractClassUse() {
        String url = launchpadURL + SLING_JAVA_USE_ABSTRACT;
        String pageContent = client.getStringContent(url, 500);
        assertTrue(pageContent.contains(
                "org.apache.sling.scripting.sightly.testing.adaptable.AbstractRequestAdapterUseObject represents an interface or an abstract class which cannot be instantiated"));
    }

    @Test
    public void testDataSlyResourceArraySelectors() {
        String url = launchpadURL + SLING_RESOURCE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("selectors: a.b", HTMLExtractor.innerHTML(url, pageContent, "#selectors span.selectors"));
        assertEquals("selectors: a.b", HTMLExtractor.innerHTML(url, pageContent, "#selectors-remove-c span.selectors"));
        assertEquals("selectors: a.c", HTMLExtractor.innerHTML(url, pageContent, "#removeselectors-remove-b span.selectors"));
        assertEquals("selectors: a.b.c", HTMLExtractor.innerHTML(url, pageContent, "#addselectors span.selectors"));
        assertEquals("It works", HTMLExtractor.innerHTML(url, pageContent, "#dot"));
        assertEquals("path: /content/sightly/text.txt", HTMLExtractor.innerHTML(url, pageContent, "#extension-selectors span.path"));
        assertEquals("selectors: a.b", HTMLExtractor.innerHTML(url, pageContent, "#extension-selectors span.selectors"));
        assertEquals("path: /content/sightly/text.txt", HTMLExtractor.innerHTML(url, pageContent, "#extension-replaceselectors span.path"));
        assertEquals("selectors: c", HTMLExtractor.innerHTML(url, pageContent, "#extension-replaceselectors span.selectors"));
    }

    @Test
    public void testDataSlyResourceResolution() {
        String url = launchpadURL + SLING_RESOURCE;
        String pageContent = client.getStringContent(url, 200);

        assertEquals("resource.with.dots.in.path", HTMLExtractor.innerHTML(url, pageContent,
                "#_content_sightly_resource_with_dots_in_path span" +
                ".name"));
        assertEquals("/content/sightly/resource.with.dots.in.path", HTMLExtractor.innerHTML(url, pageContent,
                "#_content_sightly_resource_with_dots_in_path span" +
                ".path"));
        assertEquals("false", HTMLExtractor.innerHTML(url, pageContent, "#_content_sightly_resource_with_dots_in_path span" +
                ".synthetic"));

        assertEquals("nonexistingresource", HTMLExtractor.innerHTML(url, pageContent, "#_content_sightly_nonexistingresource span" +
                ".name"));
        assertEquals("/content/sightly/nonexistingresource", HTMLExtractor.innerHTML(url, pageContent,
                "#_content_sightly_nonexistingresource span" +
                ".path"));
        assertEquals("true", HTMLExtractor.innerHTML(url, pageContent, "#_content_sightly_nonexistingresource span.synthetic"));

        assertEquals("resource", HTMLExtractor.innerHTML(url, pageContent, "#_content_sightly_resource span" +
                ".name"));
        assertEquals("/content/sightly/resource", HTMLExtractor.innerHTML(url, pageContent, "#_content_sightly_resource span" +
                ".path"));
        assertEquals("false", HTMLExtractor.innerHTML(url, pageContent, "#_content_sightly_resource span.synthetic"));
        assertEquals("", HTMLExtractor.innerHTML(url, pageContent, "#wrapper-no-recursion"));
    }

    @Test
    public void testDataSlyTemplate() {
        String url = launchpadURL + SLING_TEMPLATE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("SUCCESS", HTMLExtractor.innerHTML(url, pageContent, "#template"));
    }

    @Test
    public void testBadTemplateIdentifier() {
        String url = launchpadURL + SLING_TEMPLATE_BAD_IDENTIFIER;
        String pageContent = client.getStringContent(url, 500);
        assertTrue(pageContent.contains(
                "org.apache.sling.scripting.sightly.java.compiler.SightlyJavaCompilerException: Unsupported identifier name: bad-template-id"));
    }

    @Test
    public void testJSUseAPI() {
        String url = launchpadURL + SLING_JS_USE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("use", HTMLExtractor.innerHTML(url, pageContent, "#resource-name"));
        assertEquals("use", HTMLExtractor.innerHTML(url, pageContent, "#resource-getName"));
        assertEquals("/apps/sightly/scripts/use", HTMLExtractor.innerHTML(url, pageContent, "#resource-resourceType"));
        assertEquals("/apps/sightly/scripts/use", HTMLExtractor.innerHTML(url, pageContent, "#resource-getResourceType"));
    }

    @Test
    public void testJSUseAPISiblingDependencies() {
        String url = launchpadURL + SLING_JS_DEPENDENCY_RESOLUTION;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("/apps/sightly/scripts/siblingdeps/dependency.js", HTMLExtractor.innerHTML(url, pageContent, "#js-rep-res"));
    }

    @Test
    public void testUseAPIInheritanceOverlaying() {
        String url = launchpadURL + SLING_USE_INHERITANCE_WITHOVERLAY;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("child.javaobject", HTMLExtractor.innerHTML(url, pageContent, "#javaobj"));
        assertEquals("child.javascriptobject", HTMLExtractor.innerHTML(url, pageContent, "#javascriptobj"));
        assertEquals("child.ecmaobject", HTMLExtractor.innerHTML(url, pageContent, "#ecmaobj"));
        assertEquals("child.template", HTMLExtractor.innerHTML(url, pageContent, "#templateobj"));
        assertEquals("child.partials.javascript", HTMLExtractor.innerHTML(url, pageContent, "#partialsjs"));
        assertEquals("child.partials.ecmaobject", HTMLExtractor.innerHTML(url, pageContent, "#partialsecma"));
        assertEquals("child.partials.template", HTMLExtractor.innerHTML(url, pageContent, "#partialstemplate"));
        assertEquals("child.partials.included", HTMLExtractor.innerHTML(url, pageContent, "#partialsincluded"));
        assertEquals("child.partials.javaobject", HTMLExtractor.innerHTML(url, pageContent, "#partialsjava"));
        assertEquals("child.partials.javascript", HTMLExtractor.innerHTML(url, pageContent, "#partials-included-js"));
    }

    @Test
    public void testUseAPIInheritanceWithoutOverlay() {
        String url = launchpadURL + SLING_USE_INHERITANCE_WITHOUTOVERLAY;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("notoverlaid", HTMLExtractor.innerHTML(url, pageContent, "#notoverlaid"));
    }

    @Test
    public void testUseAPIInheritanceAndOverlay() {
        String url = launchpadURL + SLING_USE_INHERITANCE_AND_OVERLAY;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("base.js", HTMLExtractor.innerHTML(url, pageContent, "div.include-inherit"));
    }

    @Test
    public void testRepositoryPojoUpdate() throws Exception {
        String url = launchpadURL + SLING_JAVA_USE_POJO_UPDATE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("original", HTMLExtractor.innerHTML(url + System.currentTimeMillis(), pageContent, "#repopojo"));
        uploadFile("RepoPojo.java.updated", "RepoPojo.java", "/apps/sightly/scripts/use");
        Thread.sleep(1000);
        pageContent = client.getStringContent(url, 200);
        assertEquals("updated", HTMLExtractor.innerHTML(url + System.currentTimeMillis(), pageContent, "#repopojo"));
        uploadFile("RepoPojo.java.original", "RepoPojo.java", "/apps/sightly/scripts/use");
        Thread.sleep(1000);
        pageContent = client.getStringContent(url, 200);
        assertEquals("original", HTMLExtractor.innerHTML(url + System.currentTimeMillis(), pageContent, "#repopojo"));
    }

    @Test
    public void testRepositoryPojoUpdateDirty() throws Exception {
        String url = launchpadURL + SLING_JAVA_USE_POJO_UPDATE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("original", HTMLExtractor.innerHTML(url + System.currentTimeMillis(), pageContent, "#repopojo"));

        uploadFile("RepoPojo.java.updated", "RepoPojo.java", "/apps/sightly/scripts/use");
        Thread.sleep(1000);
        restartSightlyEngineBundle();

        pageContent = client.getStringContent(url, 200);
        assertEquals("updated", HTMLExtractor.innerHTML(url + System.currentTimeMillis(), pageContent, "#repopojo"));

        uploadFile("RepoPojo.java.original", "RepoPojo.java", "/apps/sightly/scripts/use");
        Thread.sleep(1000);
        restartSightlyEngineBundle();

        pageContent = client.getStringContent(url, 200);
        assertEquals("original", HTMLExtractor.innerHTML(url + System.currentTimeMillis(), pageContent, "#repopojo"));
    }

    @Test
    public void testScriptUpdate() throws Exception {
        String url = launchpadURL + SLING_SCRIPT_UPDATE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("Hello world!", HTMLExtractor.innerHTML(url + System.currentTimeMillis(), pageContent, "#update"));
        uploadFile("update.v2.html", "update.html", "/apps/sightly/scripts/update");
        Thread.sleep(1000);
        pageContent = client.getStringContent(url, 200);
        assertEquals("Hello, John Doe!", HTMLExtractor.innerHTML(url + System.currentTimeMillis(), pageContent, "#update"));
        uploadFile("update.html", "update.html", "/apps/sightly/scripts/update");
        Thread.sleep(1000);
        pageContent = client.getStringContent(url, 200);
        assertEquals("Hello world!", HTMLExtractor.innerHTML(url + System.currentTimeMillis(), pageContent, "#update"));
    }

    @Test
    public void testRepositoryPojoNoPkg() {
        String url = launchpadURL + SLING_USE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("nopkg", HTMLExtractor.innerHTML(url, pageContent, "#repopojo-nopkg"));
    }

    @Test
    public void testAttributeQuotes() {
        String url = launchpadURL + SLING_ATTRIBUTE_QUOTES;
        String pageContent = client.getStringContent(url, 200);
        // need to test against the raw content
        assertTrue(pageContent.contains("<span data-resource='{\"resource\" : " +
                "\"/content/sightly/attributequotes\"}'>/content/sightly/attributequotes</span>"));
        assertTrue(pageContent.contains("<span data-resource=\"/content/sightly/attributequotes\">/content/sightly/attributequotes</span" +
                ">"));
        assertTrue(pageContent.contains("<span data-resource=\"/content/sightly/attributequotes\">/content/sightly/attributequotes</span" +
                ">"));
    }

    @Test
    public void testCRLFNoPkg() {
        String url = launchpadURL + SLING_CRLF_NOPKG;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("nopkg", HTMLExtractor.innerHTML(url, pageContent, "#repopojocrlf-nopkg"));
    }

    @Test
    public void testCRLFPkg() {
        String url = launchpadURL + SLING_CRLF_PKG;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("pkg", HTMLExtractor.innerHTML(url, pageContent, "#repopojocrlf-pkg"));
    }

    @Test
    public void testCRLFWrongPkg() {
        String url = launchpadURL + SLING_CRLF_WRONGPKG;
        String pageContent = client.getStringContent(url, 500);
        assertTrue(pageContent.contains("Compilation errors in apps/sightly/scripts/crlf/RepoPojoWrongPkgCRLF.java"));
    }

    @Test
    public void actualResource() {
        String url = launchpadURL + SLING_RESOURCE_ACTUAL;
        String pageContent = client.getStringContent(url, 200);
        String hash = HTMLExtractor.innerHTML(url, pageContent, "#hash");
        String actual = HTMLExtractor.innerHTML(url, pageContent, "#actual");
        String path = HTMLExtractor.innerHTML(url, pageContent, "#path");
        assertEquals(hash, actual);
        assertNotEquals(hash, path);
    }

    @Test
    public void testRequestAttributes() {
        String url = launchpadURL + SLING_REQUEST_ATTRIBUTES;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("testValue", HTMLExtractor.innerHTML(url, pageContent, "#attrs-set"));
        assertEquals("", HTMLExtractor.innerHTML(url, pageContent, "#attrs-unset"));
    }

    @Test
    public void testRequestAttributesInclude() {
        String url = launchpadURL + SLING_REQUEST_ATTRIBUTES_INCLUDE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("testValue", HTMLExtractor.innerHTML(url, pageContent, "#attrs-set"));
        assertEquals("", HTMLExtractor.innerHTML(url, pageContent, "#attrs-unset"));
    }

    @Test
    public void testResourceUse() {
        String url = launchpadURL + SLING_RESOURCE_USE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("foobar-someresource", HTMLExtractor.innerHTML(url, pageContent, "#someresource .data"));
        assertEquals("foobar-somefolder", HTMLExtractor.innerHTML(url, pageContent, "#somefolder .data"));
        assertEquals("foobar-sometemplate", HTMLExtractor.innerHTML(url, pageContent, "#sometemplate .data"));
        assertEquals("foobar-somejava", HTMLExtractor.innerHTML(url, pageContent, "#somejava .data"));
        assertEquals("foobar-somejs", HTMLExtractor.innerHTML(url, pageContent, "#somejs .data"));
        assertEquals("foobar-someecma", HTMLExtractor.innerHTML(url, pageContent, "#someecma .data"));
    }

    @Test
    public void testI18nBasename() {
        String url = launchpadURL + SLING_I18N + ".basename.html";
        String pageContent = client.getStringContent(url, 200);
        assertEquals("die Bank", HTMLExtractor.innerHTML(url, pageContent, "#i18n-basename-finance"));
        assertEquals("das Ufer", HTMLExtractor.innerHTML(url, pageContent, "#i18n-nobasename"));
    }

    @Test
    public void testFormatDateWithPredefinedStyles() {
        String url = launchpadURL + SLING_FORMAT;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("01.12.1918", HTMLExtractor.innerHTML(url, pageContent, "#format-date-1"));
        assertEquals("01.12.18", HTMLExtractor.innerHTML(url, pageContent, "#format-date-2"));
        assertEquals("01.12.18", HTMLExtractor.innerHTML(url, pageContent, "#format-date-3"));
        assertEquals("Sonntag, 1. Dezember 1918", HTMLExtractor.innerHTML(url, pageContent, "#format-date-4"));
    }

    @Test
    public void testFormatStringWithIcuPlural() {
        String url = launchpadURL + SLING_FORMAT;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("0 results", HTMLExtractor.innerHTML(url, pageContent, "#format-icu-plural-zero"));
        assertEquals("1 result", HTMLExtractor.innerHTML(url, pageContent, "#format-icu-plural-one"));
        assertEquals("3 results", HTMLExtractor.innerHTML(url, pageContent, "#format-icu-plural-few"));
        assertEquals("15 results", HTMLExtractor.innerHTML(url, pageContent, "#format-icu-plural-many"));
        assertEquals("0 v&yacute;sledků", HTMLExtractor.innerHTML(url, pageContent, "#format-icu-plural-zero-cs"));
        assertEquals("1 v&yacute;sledek", HTMLExtractor.innerHTML(url, pageContent, "#format-icu-plural-one-cs"));
        assertEquals("3 v&yacute;sledky", HTMLExtractor.innerHTML(url, pageContent, "#format-icu-plural-few-cs"));
        assertEquals("15 v&yacute;sledků", HTMLExtractor.innerHTML(url, pageContent, "#format-icu-plural-many-cs"));
    }

    @Test
    public void testXSSAttributeEscaping() {
        String url = launchpadURL + TCK_XSS;
        String pageContent = client.getStringContent(url, 200);
        assertTrue(pageContent.contains("<p id=\"req-context-8\" onclick=\"console.log('red')\">Paragraph</p>"));
    }

    @Test
    public void testXSSJsonStringEscaping() {
        String url = launchpadURL + SLING_XSS;
        String pageContent = client.getStringContent(url, 200);
        String expectedScript = "{\n"
                + "  \"@context\":\"https://schema.org\",\n"
                + "  \"@type\":\"FAQPage\",\n"
                + "  \"mainEntity\":[\n"
                + "    {\n"
                + "      \"@type\":\"Question\",\n"
                + "      \"name\":\"Some question with special character \\\"':\\t\"\n"
                + "      \"acceptedAnswer\":{\n"
                + "        \"@type\":\"Answer\",\n"
                + "        \"text\":\"42\"\n"
                + "      }\n"
                + "    }\n"
                + "  ]\n"
                + "}";
        assertEquals(expectedScript, HTMLExtractor.innerHTML(url, pageContent, "script"));
    }

    @Test
    public void testWhiteSpaceExpressions() {
        String url = launchpadURL + WHITESPACE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("true", HTMLExtractor.innerHTML(url, pageContent, "#nbsp"));
        assertEquals("true", HTMLExtractor.innerHTML(url, pageContent, "#tab"));
        assertEquals("true", HTMLExtractor.innerHTML(url, pageContent, "#newline"));
    }

    @Test
    public void testSyntheticResourceResolution() {
        String url = launchpadURL + SYNTHETIC_RESOURCE;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("It works!", HTMLExtractor.innerHTML(url, pageContent, "#synthetic-resource-selector"));
    }

    @Test
    public void testPrecompiled() {
        final String title = "HTL Precompiled Scripts Test";
        String url = launchpadURL + PRECOMPILED;
        String pageContent = client.getStringContent(url, 200);
        assertEquals("/content/sightly-testing/precompiled", HTMLExtractor.innerHTML(url, pageContent, "div.precompiled > span" +
                ".internal-use-pojo-fqcn"));
        assertEquals("/content/sightly-testing/precompiled", HTMLExtractor.innerHTML(url, pageContent, "div.precompiled > span" +
                ".internal-use-pojo-scn"));
        assertEquals(title, HTMLExtractor.innerHTML(url, pageContent, "div.precompiled > span.request-adapter"));
        assertEquals(title, HTMLExtractor.innerHTML(url, pageContent, "div.precompiled > span.resource-adapter"));
        assertEquals("SUCCESS", HTMLExtractor.innerHTML(url, pageContent, "div.precompiled > span.resolver-adapter"));
        assertEquals(title, HTMLExtractor.innerHTML(url, pageContent, "div.precompiled > span.request-model"));
        assertEquals(title, HTMLExtractor.innerHTML(url, pageContent, "div.precompiled > span.resource-model"));
        assertEquals("Hello World!", HTMLExtractor.innerHTML(url, pageContent, "div.precompiled > span.test-service"));
        assertEquals("Hello, John Doe", HTMLExtractor.innerHTML(url, pageContent, "div.precompiled > span.internal-exported-sling-model"));
        assertEquals("Hello, John Doe", HTMLExtractor.innerHTML(url, pageContent, "div.precompiled > span.internal-sling-model"));
        assertEquals("Evaluating ECMA scripts works just fine!", HTMLExtractor.innerHTML(url, pageContent, "div.precompiled > span.internal-script"));
        assertEquals("Evaluating Use JS-objects works just fine!", HTMLExtractor.innerHTML(url, pageContent, "div.precompiled > span" +
                ".internal-jsuse-script"));
        assertEquals("precompiled - org/apache/sling/scripting/sightly/testing/precompiled", HTMLExtractor.innerHTML(url, pageContent, "div.precompiled > span.provided-jsuse-script"));
    }

    @Test
    public void testSlingModelError() {
        String url = launchpadURL + SLING_JAVA_USE_SLING_MODEL_ERROR;
        String pageContent = client.getStringContent(url, 500);
        assertTrue("Expected that the Sling Model would be instantiated directly via the ModelFactory.",
            pageContent.contains(
                "Could not inject private java.lang.String org.apache.sling.scripting.sightly.testing.models.ResourceResolverModel.userID"
            )
        );
    }

    @Test
    public void testSlingTemplatesAccessControlRepositoryScripts() {
        String classic = launchpadURL + "/content/sightly-testing/templates-access-control/classic.html";
        String classicPageContent = client.getStringContent(classic, 200);
        assertEquals("template loaded", HTMLExtractor.innerHTML(classic, classicPageContent, "div.wrapper > div.include-wrapper > div" +
                ".template"));
    }

    @Test
    public void testSlingTemplatesAccessControlBundledScripts() {
        String precompiled = launchpadURL + "/content/sightly-testing/templates-access-control/precompiled.html";
        String precompiledPageContent = client.getStringContent(precompiled, 200);
        assertEquals("template loaded", HTMLExtractor.innerHTML(precompiled, precompiledPageContent, "div.precompiled-wrapper > div" +
                ".precompiled-include-wrapper > div.template"));
    }

    private void restartSightlyEngineBundle() throws InterruptedException, IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(launchpadURL + "/system/console/bundles/org.apache.sling.scripting.sightly");
        // Stop bundle
        FormEntityBuilder formBuilder = FormEntityBuilder.create();
        post.setHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
        formBuilder.addParameter("action", "stop");
        post.setEntity(formBuilder.build());
        httpClient.execute(post);
        Thread.sleep(1000);
        // Start bundle
        formBuilder = FormEntityBuilder.create();
        post.setHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
        formBuilder.addParameter("action", "start");
        post.setEntity(formBuilder.build());
        httpClient.execute(post);
        Thread.sleep(1000);
    }

    private void uploadFile(String fileName, String serverFileName, String url) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(launchpadURL + url);
        post.setHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        InputStreamBody inputStreamBody = new InputStreamBody(this.getClass().getClassLoader().getResourceAsStream(fileName),
                ContentType.TEXT_PLAIN, fileName);
        entityBuilder.addPart(serverFileName, inputStreamBody);
        post.setEntity(entityBuilder.build());
        httpClient.execute(post);
    }

}
