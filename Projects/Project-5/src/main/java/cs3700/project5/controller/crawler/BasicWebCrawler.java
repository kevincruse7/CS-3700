package cs3700.project5.controller.crawler;

import cs3700.project5.Config;
import cs3700.project5.controller.http.HTTPConnection;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Basic implementation of a web crawler application.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class BasicWebCrawler implements WebCrawler {
    @NonNull
    private final HTTPConnection httpConnection;

    @NonNull
    private final String username;

    @NonNull
    private final String password;

    @NonNull
    private final List<String> flags;

    @NonNull
    private final Set<String> visitedPages;

    @NonNull
    private final Deque<String> pageDeque;

    @Override
    @SneakyThrows
    public void run() {
        flags.clear();
        visitedPages.clear();
        pageDeque.clear();

        visitedPages.add(Config.WEBSITE_LOGIN_PAGE);
        String loginPageContent = httpConnection.getResource(Config.WEBSITE_LOGIN_PAGE);

        Document loginPageDoc = Jsoup.parse(loginPageContent);

        @SuppressWarnings("SpellCheckingInspection")
        Element csrfTokenElem = loginPageDoc.selectFirst("input[name=csrfmiddlewaretoken]");
        String csrfToken = Objects.requireNonNull(csrfTokenElem).attr("value");

        Element nextParamElem = loginPageDoc.selectFirst("input[name=next]");
        String nextParam = Objects.requireNonNull(nextParamElem).attr("value");

        @SuppressWarnings("SpellCheckingInspection")
        String loginForm = String.format(
            "username=%s&password=%s&csrfmiddlewaretoken=%s&next=%s",
            username, password, csrfToken, URLEncoder.encode(nextParam, Charset.defaultCharset())
        );

        // Log in to website
        String rootPage = httpConnection.postToResource(
            Config.WEBSITE_LOGIN_PAGE.split("\\?")[0],
            loginForm
        );

        String rootPageContent = httpConnection.getResource(rootPage);
        parsePage(rootPageContent);

        // Traverse website by performing a DFS on the hyperlink graph
        while (flags.size() < Config.FLAG_COUNT && !pageDeque.isEmpty()) {
            String page = pageDeque.pop();

            if (visitedPages.contains(page)) {
                continue;
            }

            visitedPages.add(page);
            String pageContent = httpConnection.getResource(page);

            if (pageContent.equals("")) {
                continue;
            }

            parsePage(pageContent);
        }

        // Print out found flags
        for (String flag : flags) {
            System.out.println(flag);
        }
    }

    @Override
    public void close() throws IOException {
        httpConnection.close();
    }

    private void parsePage(@NonNull String pageContent) {
        Document pageDoc = Jsoup.parse(pageContent);

        // Search for flags on page
        for (Element flagElem : pageDoc.select(".secret_flag")) {
            String flag = flagElem.text().split(": ")[1];
            flags.add(flag);
        }

        // Add found links on page to search stack
        for (Element linkElem : pageDoc.select("#content a")) {
            String link = linkElem.attr("href");

            if (!visitedPages.contains(link)) {
                pageDeque.push(link);
            }
        }
    }
}
