package com.Nunbody.domain.Mail.service;

import com.Nunbody.domain.Mail.domain.MailBody;
import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.domain.Mail.dto.response.MailListResponseDto;
import com.Nunbody.domain.Mail.dto.resquest.FilterKeywordRequest;
import com.Nunbody.domain.Mail.repository.MailBodyRepository;
import com.Nunbody.domain.Mail.repository.MailRepository;
import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.ling.BasicDatum;
import edu.stanford.nlp.ling.Datum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    public List<String> categoryFilter(FilterKeywordRequest filterKeywordRequest)throws IOException {
        List<MailHeader> mailList = getMailHeaderList(filterKeywordRequest.getMemberId());

        List<MailHeader> filteredMailList = new ArrayList<>();
        List<String> list = new ArrayList<>();
        for (MailHeader mailHeader : mailList) {
            MailBody mailContent = getMailBody(mailHeader.getId());

            String htmlContent = Jsoup.parse(mailContent.getContent()).text();

            String filePath = "src/main/resources/data"; // 저장할 파일 경로

            // Dataset 생성 및 데이터 추가
            Dataset<String, String> dataset = new Dataset<>();
            dataset.add(makeDatum("쇼핑", "프리쇼핑"));
            dataset.add(makeDatum("결제", "결제안내"));

            // Dataset을 CSV 파일로 저장
            saveDatasetToFile(dataset, filePath);
            // 텍스트 분류 모델 학습
            ColumnDataClassifier classifier = new ColumnDataClassifier("src/main/resources/prop.txt"); // 프로퍼티 파일을 정의하여 사용
            classifier.trainClassifier(filePath);

            // HTML에서 텍스트 추출
            Datum<String, String> extractedText = extractTextFromHtml(htmlContent);

            // 추출한 텍스트를 통해 카테고리 예측
            String predictedCategory = classifier.classOf(extractedText);
            list.add(predictedCategory);
        }
        return list;
    }
    private static void saveDatasetToFile(Dataset<String, String> dataset, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // CSV 파일 헤더 작성
            writer.append("Label,Feature\n");

            // 각 행에 데이터 작성
            for (Datum<String, String> datum : dataset) {
                writer.append(datum.label() + "," + datum.asFeatures() + "\n");
            }

            System.out.println("Dataset saved to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Datum<String, String> extractTextFromHtml(String htmlContent) {
        Document doc = Jsoup.parse(htmlContent);
        StringBuilder textBuilder = new StringBuilder();
        for (Element element : doc.getAllElements()) {
            textBuilder.append(element.text()).append(" ");
        }

        String extractedText = textBuilder.toString().trim();

        return makeDatum("SomeLabel",extractedText);
        // 생성한 Datum 반환
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
//    private Datum<String, String> makeDatum(String label, String feature) {
//        Datum<String, String> datum = new Datum<>();
//        datum.label = label;      // 레이블 설정
//        datum.asFeatures(feature); // 피처 설정
//        return datum;
//    }
    private Datum<String, String> makeDatum(String label, String feature) {
        return new BasicDatum<>(Collections.singleton(label), feature);
    }
    private List<MailHeader> getMailHeaderList(Long memberId){
        return mailRepository.findAllByMemberId(memberId);
    }
    private MailBody getMailBody(Long mailId){
        return mailBodyRepository.findByMailId(mailId);
    }
}