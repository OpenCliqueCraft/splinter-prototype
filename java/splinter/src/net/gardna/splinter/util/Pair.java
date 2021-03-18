package net.gardna.splinter.util;

public class Pair<Left, Right> {
    public Left left;
    public Right right;

    public Pair(Left left, Right right) {
        this.left = left;
        this.right = right;
    }

    public boolean equals(Pair<Left, Right> pair) {
        return pair.left.equals(left) && pair.right.equals(right);
    }
}
