import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Pattern;


public class SiteNodeRecursiveActions extends RecursiveAction {
    private final SiteNode node;

    public SiteNodeRecursiveActions(SiteNode node) {
        this.node = node;
    }

    @Override
    protected void compute() {
        try {
            Thread.sleep(150);
            Document page = Jsoup.connect(node.getUrl())
                    .maxBodySize(0)
                    .userAgent("Chrome/103.0.5060.114")
                    .timeout(0)
                    .get();

            Elements elements = page.select("body").select("a");
            for (Element a : elements) {
                String childUrl = a.absUrl("href");
                if (isCorrectUrl(childUrl)) {
                    childUrl = stripParams(childUrl);
                    node.addChild(new SiteNode(childUrl));
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }

        for (SiteNode child : node.getChildren()) {
            SiteNodeRecursiveActions task = new SiteNodeRecursiveActions(child);
            task.compute();
        }
    }

    private String stripParams(String url) {
        return url.replaceAll("\\?.+", "");
    }

    private boolean isCorrectUrl(String url) {
        Pattern patternRoot = Pattern.compile("^" + node.getUrl());
        Pattern patternNotFile = Pattern.compile("([^\\s]+(\\.(?i)(jpg|png|gif|bmp|pdf))$)");
        Pattern patternNotAnchor = Pattern.compile("#([\\w\\-]+)?$");

        return patternRoot.matcher(url).lookingAt()
                && !patternNotFile.matcher(url).find()
                && !patternNotAnchor.matcher(url).find();
    }
}
