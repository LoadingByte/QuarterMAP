
package com.quartercode.quartermap.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

    /**
     * When the given {@link HttpURLConnection} yields a redirect code which isn't handled by java (e.g. due to a change in protocol), this method
     * recursively makes sure that even those redirects are followed.
     * The returned connection is guaranteed to yield some actual data and <b>not any kind of redirect</b>.
     * Note, however, that the returned connection always is a new one and therefore doesn't have any of the settings the initial connection had.
     *
     * @param conn The connection which should be resolved.
     * @return The new connection which resulted after following all redirects.
     * @throws IOException Something goes wrong while following the redirects.
     */
    public static HttpURLConnection resolveRedirects(HttpURLConnection conn) throws IOException {

        int status = conn.getResponseCode();

        if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
            // Get redirect URL from "Location" header field
            String newUrl = conn.getHeaderField("Location");

            // Open the new connnection again
            return resolveRedirects((HttpURLConnection) new URL(newUrl).openConnection());
        } else {
            return (HttpURLConnection) conn.getURL().openConnection();
        }
    }

    private HttpUtil() {

    }

}
