import java.util.concurrent.CopyOnWriteArrayList;

public class SiteNode {
    private volatile SiteNode parent;
    private volatile int depth;
    private String url;
    private volatile CopyOnWriteArrayList<SiteNode> children;

    public SiteNode(String url) {
        depth = 0;
        this.url = url;
        parent = null;
        children = new CopyOnWriteArrayList<>();
    }

    private int calculateDepth() {
        int result = 0;
        if (parent == null) {
            return result;
        }
        result = 1 + parent.calculateDepth();
        return result;
    }

    public synchronized void addChild(SiteNode element) {
        SiteNode root = getRootElement();
        if (!root.contains(element.getUrl())) {
            element.setParent(this);
            children.add(element);
        }
    }

    private boolean contains(String url) {
        if (this.url.equals(url)) {
            return true;
        }
        for (SiteNode child : children) {
            if (child.contains(url))
                return true;
        }

        return false;
    }

    public String getUrl() {
        return url;
    }

    private void setParent(SiteNode sitemapNode) {
        synchronized (this) {
            this.parent = sitemapNode;
            this.depth = calculateDepth();
        }
    }

    public SiteNode getRootElement() {
        return parent == null ? this : parent.getRootElement();
    }

    public CopyOnWriteArrayList<SiteNode> getChildren() {
        return children;
    }
}
