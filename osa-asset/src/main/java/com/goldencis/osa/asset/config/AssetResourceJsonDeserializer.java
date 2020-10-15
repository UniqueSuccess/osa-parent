package com.goldencis.osa.asset.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.goldencis.osa.asset.entity.AssetBs;
import com.goldencis.osa.asset.entity.AssetCs;
import com.goldencis.osa.asset.entity.AssetDb;
import com.goldencis.osa.asset.resource.AssetResource;
import com.goldencis.osa.asset.resource.AssetResourceType;
import com.goldencis.osa.asset.resource.IAssetTypeParser;
import com.goldencis.osa.common.utils.JsonUtils;
import com.goldencis.osa.common.utils.SpringUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-11-08 10:15
 **/
@Component
public class AssetResourceJsonDeserializer extends JsonDeserializer<AssetResource> {

    private IAssetTypeParser assetTypeParser;

    @Override
    public AssetResource deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (assetTypeParser == null) {
            assetTypeParser = (IAssetTypeParser) SpringUtil.getBean("assetTypeParserImpl");
        }
        JsonNode jsonNode = p.getCodec().readTree(p);
        int type = jsonNode.get("type").asInt();
        AssetResourceType resourceType = assetTypeParser.parse(type);
        if (Objects.isNull(resourceType)) {
            return null;
        }
        AssetResource resource = null;
        switch (resourceType) {
            case ASSETBS:
                resource = jsonNodeToBean(jsonNode, AssetBs.class);
                break;
            case ASSETCS:
                resource = jsonNodeToBean(jsonNode, AssetCs.class);
                break;
            case ASSETDB:
                resource = jsonNodeToBean(jsonNode, AssetDb.class);
                break;
            case ASSETNET:
                // net类型的设备目前没有额外数据
                // resource = jsonNodeToBean(jsonNode, AssetNet.class);
                break;
            default:
                throw new IllegalArgumentException("错误的设备类型 : " + type);
        }
        return resource;
    }

    private AssetResource jsonNodeToBean(JsonNode jsonNode, Class<? extends AssetResource> clazz) {
        return JsonUtils.mapToPojo(jsonNodeToMap(jsonNode, clazz), clazz);
    }

    private Map<String, Object> jsonNodeToMap(JsonNode jsonNode, Class<? extends AssetResource> clazz) {
        List<String> list = getEffectiveFieldNameList(clazz);
        return list.stream()
                .filter(s -> Objects.nonNull(jsonNode.get(s)))
                .collect(Collectors.toMap(s -> s, s -> jsonNode.get(s).asText()));
    }

    /**
     * 获取有效的字段名称列表<br>
     * 非静态,非常量
     *
     * @param clazz
     * @return
     */
    private List<String> getEffectiveFieldNameList(Class<? extends AssetResource> clazz) {
        List<String> list = new ArrayList<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                continue;
            }
            list.add(field.getName());
        }
        return list;
    }
}
