import java.io.FileOutputStream;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final String ROOT_SITE = "https://skillbox.ru/";

    public static void main(String[] args) {

        SiteNode sitemapRoot = new SiteNode(ROOT_SITE);
        new ForkJoinPool().invoke(new SiteNodeRecursiveActions(sitemapRoot));
        try (FileOutputStream stream = new FileOutputStream
                ("src/main/resources/links.txt")) {
            String result = createSitemapString(sitemapRoot, 0);
            stream.write(result.getBytes());
            stream.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String createSitemapString(SiteNode node, int depth) {
        String tabs = String.join("", Collections.nCopies(depth, "\t"));
        StringBuilder result = new StringBuilder(tabs + node.getUrl());
        node.getChildren().forEach(child -> {
            result.append("\n").append(createSitemapString(child, depth + 1));
        });
        return result.toString();
    }
}