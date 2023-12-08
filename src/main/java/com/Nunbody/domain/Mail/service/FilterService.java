package com.Nunbody.domain.Mail.service;

import com.Nunbody.domain.Mail.domain.MailBody;
import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.domain.Mail.dto.response.MailListResponseDto;
import com.Nunbody.domain.Mail.dto.resquest.FilterKeywordRequest;
import com.Nunbody.domain.Mail.repository.MailBodyRepository;
import com.Nunbody.domain.Mail.repository.MailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class FilterService {
    private final MailRepository mailRepository;
    private final MailBodyRepository mailBodyRepository;

    public List<MailListResponseDto> filterContent(FilterKeywordRequest filterKeywordRequest) {


        List<MailHeader> mailList = getMailHeaderList(filterKeywordRequest.getMemberId());
        List<MailListResponseDto> mailListResponseDtoList = createMailDtoList(mailList,filterKeywordRequest );

        return mailListResponseDtoList;
    }
    private List<MailListResponseDto> createMailDtoList(List<MailHeader> mailList, FilterKeywordRequest filterKeywordRequest){
        List<MailHeader> filteredMailList = new ArrayList<>();

        for (MailHeader mailHeader : mailList) {
            MailBody mailContent = getMailBody(mailHeader.getId());

            Document doc = Jsoup.parse(mailContent.getContent());
            Elements links = doc.select("a");
            links.remove();

            // 다른 필터링 작업을 수행할 수 있음
            // 예: 특정 태그 내의 내용 중 키워드를 포함하는 부분만 남기고 나머지는 제거
            Elements paragraphs = doc.select("p");
            boolean containsAnyKeyword = false;
            for (Element paragraph : paragraphs) {
                String paragraphText = paragraph.text();
                // 키워드 중 어느 하나라도 포함하는 경우 해당 부분만 남기고 나머지는 제거
                if (filterKeywordRequest.getKeywords().stream().anyMatch(paragraphText.toLowerCase()::contains)) {
                    paragraph.html(paragraphText);
                    containsAnyKeyword = true;
                } else {
                    paragraph.remove(); // 키워드를 포함하지 않는 경우 해당 요소를 제거
                }
            }

            if (containsAnyKeyword) {
                // 키워드를 포함하는 경우 해당 mailHeader를 리스트에 추가
                filteredMailList.add(mailHeader);
            }
        }
        return filteredMailList.stream().map(mailHeader -> MailListResponseDto.of(mailHeader))
                .collect(Collectors.toList());
    }
    private List<MailHeader> getMailHeaderList(Long memberId){
        return mailRepository.findAllByMemberId(memberId);
    }
    private MailBody getMailBody(Long mailId){
        return mailBodyRepository.findByMailId(mailId);
    }
}