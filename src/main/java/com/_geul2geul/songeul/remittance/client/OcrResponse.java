package com._geul2geul.songeul.remittance.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcrResponse {

    private OcrFieldResult name;
    private OcrFieldResult bank;
    private OcrFieldResult account;
    private OcrFieldResult amount;

}
