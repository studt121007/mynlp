package com.mayabot.nlp.collection.bintrie;

import java.util.Map;
import java.util.function.Function;

public enum BinTrieTreeBuilder {

    _default(false, HashTrieNode::new),
    bigArray(false, ArrayTrieNode::new),
    bigHash(false, ArrayTrieNode::new),
    miniArray(true, ArrayTrieNode::new),
    miniHash(true, HashTrieNode::new);


    private boolean rootUseMap;
    BinTrieTree.TrieNodeFactory nodeFactory;


    BinTrieTreeBuilder(boolean rootUseMap, BinTrieTree.TrieNodeFactory nodeFactory) {
        this.rootUseMap = rootUseMap;
        this.nodeFactory = nodeFactory;
    }

    public <T> BinTrieTree<T> build() {
        return new BinTrieTree<>(rootUseMap, nodeFactory);
    }

    public <T> BinTrieTree<T> build(Map<String, T> map ) {

        BinTrieTree tree = new BinTrieTree<>(rootUseMap, nodeFactory);

        for (Map.Entry<String, T> entry : map.entrySet()) {
            tree.put(entry.getKey(), entry.getValue());
        }
        return tree;
    }

    /**
     * 每行的格式 word[空格]args
     * @param lines
     * @param transFun
     * @param <T>
     * @return
     */
    public <T> BinTrieTree<T> build(String split, Iterable<String> lines , Function<String[],T> transFun) {

        BinTrieTree<T> tree = new BinTrieTree<>(rootUseMap, nodeFactory);

        final String[] empty = new String[0];
        for(String line : lines){
            String[] list = line.split(split);
            if (list.length == 1) {
                tree.put(list[0],transFun.apply(empty));
            }else{
                String[] sublist = new String[list.length-1];
                System.arraycopy(list,1,sublist,0,sublist.length);
                tree.put(list[0], transFun.apply(sublist));
            }
        }
        return tree;
    }
}
