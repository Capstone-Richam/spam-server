package com.Nunbody.domain.member.domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


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
