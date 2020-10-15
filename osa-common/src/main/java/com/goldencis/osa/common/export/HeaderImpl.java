package com.goldencis.osa.common.export;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-10-31 14:22
 **/
public class HeaderImpl implements IHeader {
    /**
     * 内容
     */
    private String content;
    /**
     * 排序
     */
    private int order;

    public HeaderImpl(String content, int order) {
        this.content = content;
        this.order = order;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String content() {
        return content;
    }

    @Override
    public int order() {
        return order;
    }

    @Override
    public int compareTo(IHeader o) {
        return this.order() - o.order();
    }
}
