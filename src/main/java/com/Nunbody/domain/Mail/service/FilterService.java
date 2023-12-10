package com.Nunbody.domain.Mail.service;

import com.Nunbody.domain.Mail.domain.MailBody;
import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.domain.Mail.dto.response.FilterMailListResponseDto;
import com.Nunbody.domain.Mail.dto.resquest.FilterKeywordRequest;
import com.Nunbody.domain.Mail.repository.MailBodyRepository;
import com.Nunbody.domain.Mail.repository.MailRepository;
import com.Nunbody.global.config.auth.MemberId;
import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.ling.BasicDatum;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.stats.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
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

    public Page<FilterMailListResponseDto> filterContent( Long memberId,FilterKeywordRequest filterKeywordRequest, Pageable pageable) {


        List<MailHeader> mailList = getMailHeaderList(memberId);
        Page<FilterMailListResponseDto> filterMailListResponseDtoList = createMailDtoPage(mailList,filterKeywordRequest, pageable );

        return filterMailListResponseDtoList;
    }
   public List<String> categoryFilter(Long memberId,FilterKeywordRequest filterKeywordRequest)throws IOException {
        List<MailHeader> mailList = getMailHeaderList(memberId);

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
            Datum<String,String> example=makeDatum("쇼핑", "프리쇼핑");
            // Dataset을 CSV 파일로 저장
            saveDatasetToFile(dataset, filePath);
//            String outputSVMlightFilePath="src/main/resources/data2";
//            convertCSVtoSVMlight(filePath, outputSVMlightFilePath);

            // 텍스트 분류 모델 학습
//            ColumnDataClassifier classifier = new ColumnDataClassifier("src/main/resources/prop.txt"); // 프로퍼티 파일을 정의하여 사용
//            classifier.trainClassifier(outputSVMlightFilePath);
//
//            // HTML에서 텍스트 추출
//            Datum<String, String> extractedText = extractTextFromHtml(htmlContent);

            // 추출한 텍스트를 통해 카테고리 예측
//            Classifier<String,String> classifier1 = new Classifier<String, String>() ;
            String predictedCategory = classOf(example);
            list.add(predictedCategory);
        }
        return list;
    }
    public String classOf(Datum<String,String> example) {
        ColumnDataClassifier classifier = new ColumnDataClassifier("src/main/resources/prop.txt");
        return classifier.classOf(example);
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
    private Page<FilterMailListResponseDto> createMailDtoPage(List<MailHeader> mailList, FilterKeywordRequest filterKeywordRequest,Pageable pageable){
        List<MailHeader> filteredMailList = new ArrayList<>();

        for (MailHeader mailHeader : mailList) {
            MailBody mailContent = getMailBody(mailHeader.getId());

            if (mailContent == null || mailContent.getContent() == null) {

                continue;
            }
            Document doc = Jsoup.parse(mailContent.getContent());

            Elements paragraphs = doc.select("div,p,tr,br,span,td,table");
            boolean containsAnyKeyword = false;
            String topKeyword = null;

            for (Element paragraph : paragraphs) {
                String paragraphText = paragraph.text();

                if (filterKeywordRequest.getKeywords().stream().anyMatch(paragraphText.toLowerCase()::contains)) {
                    paragraph.html(paragraphText);
                    containsAnyKeyword = true;


                    String keyword = findTopKeyword(filterKeywordRequest.getKeywords(), paragraphs);
                    if (keyword != null) {
                        topKeyword = keyword;
                    }
                } else {
                    paragraph.remove();
                }
            }

            if (containsAnyKeyword) {

                mailHeader.updateTopKeyword(topKeyword);

                filteredMailList.add(mailHeader);
            }
        }

        List<FilterMailListResponseDto> filterMailListResponseDtoList=createFilterMailListResponseDtoList(filteredMailList);
        Page<FilterMailListResponseDto> resultPage = new PageImpl<>(filterMailListResponseDtoList, pageable,filterMailListResponseDtoList.size());
        return resultPage;
    }
    private List<FilterMailListResponseDto> createFilterMailListResponseDtoList(List<MailHeader> filteredMailList){
        return filteredMailList.stream().map(mailHeader -> FilterMailListResponseDto.of(mailHeader, mailHeader.getTopKeyword()))
                .collect(Collectors.toList());
    }
    private String findTopKeyword(List<String> keywords, Elements paragraphs) {
        // 키워드 중에서 paragraphs에서 가장 많이 등장하는 키워드 찾기
        // 이 부분은 실제 비즈니스 로직 및 알고리즘에 따라 다르게 구현해야 합니다.
        // 여기서는 간단히 paragraphs에서 각 키워드의 등장 횟수를 세고,
        // 가장 많이 등장한 키워드를 반환하는 것으로 가정합니다.

        String topKeyword = null;
        int maxCount = 0;

        for (String keyword : keywords) {
            int count = countKeywordOccurrences(keyword, paragraphs);
            if (count > maxCount) {
                maxCount = count;
                topKeyword = keyword;
            }
        }

        return topKeyword;
    }
    private int countKeywordOccurrences(String keyword, Elements paragraphs) {
        // 실제 키워드 등장 횟수를 세는 로직을 구현
        // 여기서는 간단히 paragraphs에서 keyword가 등장한 횟수를 세는 것으로 가정
        int count = 0;

        for (Element paragraph : paragraphs) {
            String paragraphText = paragraph.text();
            if (paragraphText.toLowerCase().contains(keyword.toLowerCase())) {
                count++;
            }
        }

        return count;
    }
// 나머지 코드는 이전에 설명한대로 유지되어야 합니다.

    //    private Datum<String, String> makeDatum(String label, String feature) {
//        Datum<String, String> datum = new Datum<>();
//        datum.label = label;      // 레이블 설정
//        datum.asFeatures(feature); // 피처 설정
//        return datum;
//    }
    public static void convertCSVtoSVMlight(String inputFilePath, String outputFilePath) {
    try {
        BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath));

        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");  // CSV 파일에서 각 줄을 쉼표로 분리

            // 첫 번째 열은 클래스 레이블
            String label = parts[0].trim();
            bw.write(label);

            // 나머지는 특성:값 쌍으로 변환하여 추가
            for (int i = 1; i < parts.length; i++) {
                String[] featureValuePair = parts[i].split(":");
                String featureIndex = featureValuePair[0].trim();
                String featureValue = featureValuePair[1].trim();
                bw.write(" " + featureIndex + ":" + featureValue);
            }


            bw.newLine();  // 다음 줄로 이동
        }

        br.close();
        bw.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    private Datum<String, String> makeDatum(String label, String feature) {
        return new BasicDatum<>(Collections.singleton(label), feature);
    }
    private List<MailHeader> getMailHeaderList(Long memberId){
        return mailRepository.findAllByMemberIdOrderByDateDesc(memberId);
    }
    private MailBody getMailBody(Long mailId){
        return mailBodyRepository.findByMailId(mailId);
    }
}