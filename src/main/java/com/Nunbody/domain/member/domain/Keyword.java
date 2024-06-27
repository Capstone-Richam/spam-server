package com.Nunbody.domain.member.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.*;
import java.util.List;


@Data
@Builder
@Document(collection = "keyword")
public class Keyword {
    @Id
    private String id;
    @Field(name = "member_id")
    private Long memberId;
    @Field(name = "words")
    private List<String> words;
}
