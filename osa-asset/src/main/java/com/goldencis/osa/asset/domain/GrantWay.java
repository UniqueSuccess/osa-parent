package com.goldencis.osa.asset.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 授权来源(授权渠道)
 * @program: osa-parent
 * @author: wang mc
 * @create: 2018-12-20 16:38
 **/
@Data
public class GrantWay {
    @JsonProperty(value = "from")
    private String grantFrom;
    @JsonProperty(value = "to")
    private String grantTo;

}
