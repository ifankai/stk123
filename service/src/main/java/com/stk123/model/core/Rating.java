package com.stk123.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Rating {
    private Integer total;
    @Getter
    private Node rating = new Node("stock", null);

    @Setter
    private String include;

    public Rating(){}

    public Rating(String include){
        this.include = include;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    class Node{
        String type;
        @JsonIgnore
        Supplier<Integer> formula;
        Integer score;
        List<Node> nodes;
        @JsonIgnore
        Node parent;

        Node(String type, Supplier<Integer> formula){
            this.type = type;
            this.formula = formula;
        }

        String getPath(){
            String path = type;
            Node parent = this.getParent();
            while(parent != null){
                path = parent.getType() + "." + path;
                parent = parent.getParent();
            }
            return path;
        }

        Node find(String type){
            if(type.equals(this.type)){
                return this;
            }
            if(nodes != null){
                for(Node node : nodes){
                    Node result = node.find(type);
                    if(result != null){
                        return result;
                    }
                }
            }
            return null;
        }

        void addChild(String type, Supplier<Integer> supplier){
            if(nodes == null){
                nodes = new ArrayList<>();
            }
            Node child = new Node(type, supplier);
            child.setParent(this);
            nodes.add(child);
        }

        int calculate(){
            int score = 0;
            if(nodes == null && this.formula != null){
                if(include == null){
                    return this.score = this.formula.get();
                }else {
                    if(StringUtils.containsIgnoreCase(this.getPath(), include))
                        return this.score = this.formula.get();
                }
            }else {
                for (Node node : nodes) {
                    score += node.calculate();
                }
            }
            this.score = score;
            return this.score;
        }

    }

    public int calculate(){
        total = rating.calculate();
        return this.total;
    }

    public void addScore(String type){
        addScore(rating.getType(), type, null);
    }
    public void addScore(String parentType, String type) {
        addScore(parentType, type, null);
    }
    public void addScore(String parentType, String type, Supplier<Integer> formula){
        Node parent = rating.find(parentType);
        parent.addChild(type, formula);
    }

    public int getScore(){
        if(total == null) {
            total = this.calculate();
        }
        return total;
    }

    public String toHtml(){
        StringBuilder builder = new StringBuilder();
        dumpNode(builder, rating, "- ");
        return builder.toString();
    }

    private void dumpNode(StringBuilder builder, Node node, String prefix) {
        if (node != null) {
            builder.append(prefix);
            builder.append(node.getType()+":"+node.getScore());
            builder.append("<br/>\n");
            prefix = "&nbsp;" + prefix;

            if(node.getNodes() != null)
                for (Node child : node.getNodes()) {
                    dumpNode(builder, child, prefix);
                }
        }

    }

    public Map toMap(){
        Map map = new LinkedHashMap();
        dumpNode(map, rating);
        return map;
    }
    private void dumpNode(Map map, Node node){
        if (node != null) {
            if(node.getNodes() != null) {
                List list = new ArrayList();
                map.put("type", node.getType());
                map.put("score", node.getScore());
                map.put("nodes", list);
                for (Node child : node.getNodes()) {
                    Map childMap = new LinkedHashMap();
                    list.add(childMap);
                    dumpNode(childMap, child);
                }
            }else{
                map.put("type", node.getType());
                map.put("score", node.getScore());
            }
        }
    }


}