package com.zj.expressway.listener;

import com.zj.expressway.tree.Node;

import java.util.List;

public interface ContractorListener {
    void returnData(List<Node> allsCache, List<Node> alls, int point, String userId);
}
