package com.mzr.tort.core.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;

public class ObjectToStringJsonDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        TreeNode treeNode = jp.getCodec().readTree(jp);

        if (treeNode instanceof ContainerNode) {
            ContainerNode containerNode = ContainerNode.class.cast(treeNode);
            return containerNode.toString();

        } else if (treeNode instanceof TextNode) {
            TextNode textNode = TextNode.class.cast(treeNode);
            return textNode.asText();

        } else {
            return treeNode.toString();
        }
    }
}
