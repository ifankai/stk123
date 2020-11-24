package com.stk123.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextDto {

    private Long id;
    private String title;
    private String text;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date insertTime;

}
