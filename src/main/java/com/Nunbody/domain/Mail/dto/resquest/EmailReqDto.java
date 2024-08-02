package com.Nunbody.domain.Mail.dto.resquest;

public record EmailReqDto(
        String platform,
        String mail,
        String header,
        String body
) {
}
