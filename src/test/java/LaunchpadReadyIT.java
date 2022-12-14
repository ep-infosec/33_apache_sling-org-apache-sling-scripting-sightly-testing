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

import java.util.List;
import java.util.Map;

import org.apache.felix.utils.json.JSONParser;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.sling.testing.junit.rules.SlingInstanceRule;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class LaunchpadReadyIT {

    private static final int LAUNCHPAD_PORT = Integer.getInteger("launchpad.http.port", 8080);
    private static final int EXPECTED_BUNDLES_COUNT = Integer.getInteger("IT.expected.bundles.count", Integer.MAX_VALUE);
    private HttpClientContext httpClientContext;

    @ClassRule
    public static final SlingInstanceRule SLING_INSTANCE_RULE = new SlingInstanceRule();

    @ClassRule
    public static LaunchpadReadyRule LAUNCHPAD = new LaunchpadReadyRule(LAUNCHPAD_PORT);

    @Before
    public void prepareHttpContext() {

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin", "admin");
        credsProvider.setCredentials(new AuthScope("localhost", LAUNCHPAD_PORT), creds);

        BasicAuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(new HttpHost("localhost", LAUNCHPAD_PORT, "http"), basicAuth);

        httpClientContext = HttpClientContext.create();
        httpClientContext.setCredentialsProvider(credsProvider);
        httpClientContext.setAuthCache(authCache);
    }

    private CloseableHttpClient newClient() {

        return HttpClientBuilder.create()
                .setDefaultCredentialsProvider(httpClientContext.getCredentialsProvider())
                .build();
    }

    @Test
    public void verifyAllBundlesStarted() throws Exception {

        try (CloseableHttpClient client = newClient()) {

            HttpGet get = new HttpGet("http://localhost:" + LAUNCHPAD_PORT + "/system/console/bundles.json");

            // pass the context to ensure preemptive basic auth is used
            // https://hc.apache.org/httpcomponents-client-ga/tutorial/html/authentication.html
            try (CloseableHttpResponse response = client.execute(get, httpClientContext)) {

                if (response.getStatusLine().getStatusCode() != 200) {
                    fail("Unexpected status line " + response.getStatusLine());
                }

                Header contentType = response.getFirstHeader("Content-Type");
                assertThat("Content-Type header", contentType.getValue(), CoreMatchers.startsWith("application/json"));

                Map<String, Object> obj = new JSONParser(response.getEntity().getContent()).getParsed();

                @SuppressWarnings("unchecked")
                List<Object> status = (List<Object>) obj.get("s");

                @SuppressWarnings("unchecked")
                List<Object> bundles = (List<Object>) obj.get("data");
                if (bundles.size() < EXPECTED_BUNDLES_COUNT) {
                    fail("Expected at least " + EXPECTED_BUNDLES_COUNT + " bundles, got " + bundles.size());
                }

                BundleStatus bs = new BundleStatus(status);

                if (bs.resolvedBundles != 0 || bs.installedBundles != 0) {

                    StringBuilder out = new StringBuilder();
                    out.append("Expected all bundles to be active, but instead got ")
                            .append(bs.resolvedBundles).append(" resolved bundles, ")
                            .append(bs.installedBundles).append(" installed bundlles: ");

                    for (Object o : bundles) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> bundle = (Map<String, Object>) o;

                        String bundleState = (String) bundle.get("state");
                        String bundleSymbolicName = (String) bundle.get("symbolicName");
                        String bundleVersion = (String) bundle.get("version");

                        switch (bundleState) {
                            case "Active":
                            case "Fragment":
                                continue;

                            default:
                                out.append("\n- ").append(bundleSymbolicName).append(" ").append(bundleVersion).append(" is in state ")
                                        .append(bundleState);
                        }
                    }

                    fail(out.toString());
                }
            }
        }
    }

    private static class BundleStatus {

        long totalBundles;
        long activeBundles;
        long activeFragments;
        long resolvedBundles;
        long installedBundles;

        public BundleStatus(List<Object> array) {

            totalBundles = (Long) array.get(0);
            activeBundles = (Long) array.get(1);
            activeFragments = (Long) array.get(2);
            resolvedBundles = (Long) array.get(3);
            installedBundles = (Long) array.get(4);

        }
    }

}
