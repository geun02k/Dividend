package com.example.dividend.model;

import lombok.Builder;
import lombok.Data;

@Data // getter, setter, toString, equalHashcode, requiredArgsConstructor 어노테이션을 포함한다.
@Builder //
public class Company {

    private String ticker;
    private String name;

}
