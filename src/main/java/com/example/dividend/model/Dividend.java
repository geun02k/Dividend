package com.example.dividend.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // getter, setter, toString, equalHashcode, requiredArgsConstructor 어노테이션을 포함한다.
//@Builder // 멤버 변수가 많지 않기 때문에 Builder 패턴이 효율적인 클래스는 아니라 사용하지 않고 생성자 이용.
@NoArgsConstructor
@AllArgsConstructor
public class Dividend {

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime date;
    private String dividend;
}
