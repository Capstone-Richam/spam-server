package com.Nunbody.domain.member.service;

import com.Nunbody.domain.member.domain.Keyword;
import com.Nunbody.domain.member.dto.KeywordRequestDto;
import com.Nunbody.domain.member.repository.KeywordRepository;
import java.util.List;

import com.Nunbody.global.config.auth.MemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;

    public void addKeyword( Long memberId, KeywordRequestDto keywordRequestDto) {
        Keyword keyword = keywordRepository.findByMemberId(memberId).orElse(null);

        if(keyword!=null) {
            keyword.getWords().addAll(keywordRequestDto.getWords());
            keywordRepository.save(keyword);
        }
    }

    public void deleteKeyword( Long memberId, KeywordRequestDto keywordRequestDto) {
        Keyword keyword = keywordRepository.findByMemberId(memberId).get();

        if(keyword!=null){
            keyword.getWords().removeAll(keywordRequestDto.getWords());
            keywordRepository.save(keyword);
        }
    }

    public List<String> getKeyword(Long memberId) {
        Keyword keyword = keywordRepository.findByMemberId(memberId).orElse(null);

        List<String> words = keyword.getWords();
        return words;
    }
}
