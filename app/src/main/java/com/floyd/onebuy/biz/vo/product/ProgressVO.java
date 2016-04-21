package com.floyd.onebuy.biz.vo.product;

/**
 * Created by floyd on 16-4-17.
 */
public class ProgressVO {
    public long id;
    public int total;
    public int left;

    public int getPrecent() {
        return (int)(this.total - this.left) * 100/this.total;
    }
}
