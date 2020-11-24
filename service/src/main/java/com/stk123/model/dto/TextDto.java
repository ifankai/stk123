package com.stk123.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.util.Date;

@Data
public class TextDto {

    private Long id;
    private String title;
    private String text;
    private Date insertTime;

}
