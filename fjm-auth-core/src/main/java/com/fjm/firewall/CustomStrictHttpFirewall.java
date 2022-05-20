package com.fjm.firewall;

import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.RequestRejectedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Author: fongjinming
 * @CreateTime: 2020-02-25 15:18
 * @Description:
 */
public class CustomStrictHttpFirewall implements HttpFirewall {

    private static final String ENCODED_PERCENT = "%25";
    private static final String PERCENT = "%";
    private static final List<String> FORBIDDEN_ENCODED_PERIOD = Collections.unmodifiableList(Arrays.asList("%2e", "%2E"));
    private static final List<String> FORBIDDEN_SEMICOLON = Collections.unmodifiableList(Arrays.asList(";", "%3b", "%3B"));
    private static final List<String> FORBIDDEN_FORWARDSLASH = Collections.unmodifiableList(Arrays.asList("%2f", "%2F"));
    private static final List<String> FORBIDDEN_BACKSLASH = Collections.unmodifiableList(Arrays.asList("\\", "%5c", "%5C"));
    private Set<String> encodedUrlBlacklist = new HashSet();
    private Set<String> decodedUrlBlacklist = new HashSet();

    public CustomStrictHttpFirewall() {
        this.urlBlacklistsAddAll(FORBIDDEN_SEMICOLON);
        this.urlBlacklistsAddAll(FORBIDDEN_FORWARDSLASH);
        this.urlBlacklistsAddAll(FORBIDDEN_BACKSLASH);
        this.encodedUrlBlacklist.add("%25");
        this.encodedUrlBlacklist.addAll(FORBIDDEN_ENCODED_PERIOD);
        this.decodedUrlBlacklist.add("%");
    }

    public void setAllowSemicolon(boolean allowSemicolon) {
        if (allowSemicolon) {
            this.urlBlacklistsRemoveAll(FORBIDDEN_SEMICOLON);
        } else {
            this.urlBlacklistsAddAll(FORBIDDEN_SEMICOLON);
        }

    }

    public void setAllowUrlEncodedSlash(boolean allowUrlEncodedSlash) {
        if (allowUrlEncodedSlash) {
            this.urlBlacklistsRemoveAll(FORBIDDEN_FORWARDSLASH);
        } else {
            this.urlBlacklistsAddAll(FORBIDDEN_FORWARDSLASH);
        }

    }

    public void setAllowUrlEncodedPeriod(boolean allowUrlEncodedPeriod) {
        if (allowUrlEncodedPeriod) {
            this.encodedUrlBlacklist.removeAll(FORBIDDEN_ENCODED_PERIOD);
        } else {
            this.encodedUrlBlacklist.addAll(FORBIDDEN_ENCODED_PERIOD);
        }

    }

    public void setAllowBackSlash(boolean allowBackSlash) {
        if (allowBackSlash) {
            this.urlBlacklistsRemoveAll(FORBIDDEN_BACKSLASH);
        } else {
            this.urlBlacklistsAddAll(FORBIDDEN_BACKSLASH);
        }

    }

    public void setAllowUrlEncodedPercent(boolean allowUrlEncodedPercent) {
        if (allowUrlEncodedPercent) {
            this.encodedUrlBlacklist.remove("%25");
            this.decodedUrlBlacklist.remove("%");
        } else {
            this.encodedUrlBlacklist.add("%25");
            this.decodedUrlBlacklist.add("%");
        }

    }

    private void urlBlacklistsAddAll(Collection<String> values) {
        this.encodedUrlBlacklist.addAll(values);
        this.decodedUrlBlacklist.addAll(values);
    }

    private void urlBlacklistsRemoveAll(Collection<String> values) {
        this.encodedUrlBlacklist.removeAll(values);
        this.decodedUrlBlacklist.removeAll(values);
    }

    @Override
    public FirewalledRequest getFirewalledRequest(HttpServletRequest request) throws RequestRejectedException {
        this.rejectedBlacklistedUrls(request);
        if (!isNormalized(request)) {
            throw new RequestRejectedException("The request was rejected because the URL was not normalized.");
        } else {
            String requestUri = request.getRequestURI();
            if (!containsOnlyPrintableAsciiCharacters(requestUri)) {
                throw new RequestRejectedException("The requestURI was rejected because it can only contain printable ASCII characters.");
            } else {
                return new FirewalledRequest(request) {
                    @Override
                    public void reset() {
                    }
                };
            }
        }
    }

    private void rejectedBlacklistedUrls(HttpServletRequest request) {
        Iterator var2 = this.encodedUrlBlacklist.iterator();

        String forbidden;
        do {
            if (!var2.hasNext()) {
                var2 = this.decodedUrlBlacklist.iterator();

                do {
                    if (!var2.hasNext()) {
                        return;
                    }

                    forbidden = (String) var2.next();
                } while (!decodedUrlContains(request, forbidden));

                throw new RequestRejectedException("The request was rejected because the URL contained a potentially malicious String \"" + forbidden + "\"");
            }

            forbidden = (String) var2.next();
        } while (!encodedUrlContains(request, forbidden));

        throw new RequestRejectedException("The request was rejected because the URL contained a potentially malicious String \"" + forbidden + "\"");
    }

    @Override
    public HttpServletResponse getFirewalledResponse(HttpServletResponse response) {
        return new FirewalledResponse(response);
    }

    private static boolean isNormalized(HttpServletRequest request) {
        if (!isNormalized(request.getRequestURI())) {
            return false;
        } else if (!isNormalized(request.getContextPath())) {
            return false;
        } else if (!isNormalized(request.getServletPath())) {
            return false;
        } else {
            return isNormalized(request.getPathInfo());
        }
    }

    private static boolean encodedUrlContains(HttpServletRequest request, String value) {
        return valueContains(request.getContextPath(), value) ? true : valueContains(request.getRequestURI(), value);
    }

    private static boolean decodedUrlContains(HttpServletRequest request, String value) {
        if (valueContains(request.getServletPath(), value)) {
            return true;
        } else {
            return valueContains(request.getPathInfo(), value);
        }
    }

    private static boolean containsOnlyPrintableAsciiCharacters(String uri) {
        int length = uri.length();

        for (int i = 0; i < length; ++i) {
            char c = uri.charAt(i);
            if (c < ' ' || c > '~') {
                return false;
            }
        }

        return true;
    }

    private static boolean valueContains(String value, String contains) {
        return value != null && value.contains(contains);
    }

    private static boolean isNormalized(String path) {
        if (path == null) {
            return true;
        } else {
            int i;
            for (int j = path.length(); j > 0; j = i) {
                i = path.lastIndexOf(47, j - 1);
                int gap = j - i;
                if (gap == 2 && path.charAt(i + 1) == '.') {
                    return false;
                }

                if (gap == 3 && path.charAt(i + 1) == '.' && path.charAt(i + 2) == '.') {
                    return false;
                }
            }

            return true;
        }
    }
}
