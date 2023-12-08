package com.Nunbody.domain.member.service;

import com.Nunbody.domain.member.domain.Keyword;
import com.Nunbody.domain.member.dto.KeywordRequestDto;
import com.Nunbody.domain.member.repository.KeywordRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;
    public void create(KeywordRequestDto keywordRequestDto) {
        Keyword keyword = Keyword.builder()
                .memberId(keywordRequestDto.getMemberId())
                .words(keywordRequestDto.getWords())
                .build();

        keywordRepository.insert(keyword);

    }

    public void addKeyword(KeywordRequestDto keywordRequestDto) {
        Keyword keyword = keywordRepository.findByMemberId(keywordRequestDto.getMemberId()).orElse(null);

        if(keyword!=null) {
            keyword.getWords().addAll(keywordRequestDto.getWords());
            keywordRepository.save(keyword);
        }
    }

    public void deleteKeyword(KeywordRequestDto keywordRequestDto) {
        Keyword keyword = keywordRepository.findByMemberId(keywordRequestDto.getMemberId()).get();

        if(keyword!=null){
            keyword.getWords().removeAll(keywordRequestDto.getWords());
            keywordRepository.save(keyword);
        }
    }
}
