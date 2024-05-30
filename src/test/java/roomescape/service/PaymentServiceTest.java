package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.config.PaymentConfig;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;

@RestClientTest(PaymentConfig.class)
class PaymentServiceTest {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MockRestServiceServer mockServer;

    @DisplayName("결제가 이상없이 승인되면 올바른 응답을 반환한다.")
    @Test
    void paymentSuccess() {
        PaymentRequest request = new PaymentRequest("randomOrderId", 10000, "randomPaymentKey");
        String expectedResponse = """
        {
            "paymentKey" : "randomPaymentKey",
            "totalAmount" : 10000
        }
        """;
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedResponse));

        PaymentResponse result = paymentService.pay(request);
        assertAll(
                () -> assertThat(result.paymentKey()).isEqualTo("randomPaymentKey"),
                () -> assertThat(result.totalAmount()).isEqualTo(10000)
        );
    }
}
