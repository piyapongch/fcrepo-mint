/**
 * Piyapong Charoenwattana
 * Project: fcrepo4-oaiprovider
 */

package org.fcrepo.mint;

import static com.codahale.metrics.MetricRegistry.name;
import static com.google.common.base.Joiner.on;
import static com.google.common.base.Splitter.fixedLength;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.fcrepo.metrics.RegistryService;
import org.slf4j.Logger;

import com.codahale.metrics.Timer;

/**
 * The NoidPathMinter class.
 *
 * @author <a href="mailto:piyapongch@gmail.com">Piyapong Charoenwattana</a>
 */
public class NoidPathMinter extends HttpPidMinter {

    private static final Logger LOGGER = getLogger(NoidPathMinter.class);

    static final Timer timer = RegistryService.getInstance().getMetrics().timer(name(NoidPathMinter.class, "mint"));

    /**
     * The NoidPathMinter class constructor.
     * @param url
     * @param method
     * @param username
     * @param password
     * @param regex
     * @param xpath
     */
    public NoidPathMinter(final String url, final String method, final String username, final String password,
        final String regex, final String xpath) {
        super(url, method, username, password, regex, xpath);
    }

    /**
     * The NoidPathMinter class constructor.
     * @param url
     * @param method
     * @throws IOException
     */
    public NoidPathMinter(final String url, final String method) {
        super(url, method, null, null, null, null);
    }

    /**
     * The init method.
     * @throws IOException
     */
    @PostConstruct
    public void init() throws IOException {
        Runtime.getRuntime().exec("/Users/pcharoen/.go/bin/noids --storage /Users/pcharoen/noid_pool");
    }

    /**
     * The destroy method.
     * @throws IOException
     */
    @PreDestroy
    public void destroy() throws IOException {
        Runtime.getRuntime().exec("pkill noids");
    }

    /**
     * Remove unwanted text from the minter service response to produce the desired identifier. Override this method for
     * processing more complex than a simple regex replacement.
     * @param responseText the response text
     * @throws IOException if exception occurred
     * @return the response
     **/
    @Override
    protected String responseToPid(final String responseText) throws IOException {
        try (final Timer.Context context = timer.time()) {
            final String s = responseText.replaceAll("\\[|\"|\\]", "");
            LOGGER.debug("Noid: " + s);
            final Iterable<String> split = fixedLength(2).split(s.substring(0, 2 * 4));
            return on("/").join(split) + "/" + s;
        }
    }

}
