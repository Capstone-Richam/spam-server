package com.Nunbody.domain.Windyflo.service;

import com.Nunbody.domain.Windyflo.dto.req.WindyfloReq;
import com.Nunbody.external.ConversationQARes;
import com.Nunbody.external.WindyfloMailClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WindyfloService {
    private final WindyfloMailClient windyfloMailClient;
    public ConversationQARes createMail(WindyfloReq windyfloReq){
        ConversationQARes result = windyfloMailClient.findMailInVectorDB(windyfloReq.prompt());
        if(result.getText().equals("Hmm, I'm not sure.")){
            result = windyfloMailClient.createMail(windyfloReq.prompt());
        }
        return result;
    }
}
