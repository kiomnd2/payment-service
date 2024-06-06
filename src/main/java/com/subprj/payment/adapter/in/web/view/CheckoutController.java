package com.subprj.payment.adapter.in.web.view;

import com.subprj.payment.application.port.in.CheckoutCommand;
import com.subprj.payment.application.port.in.CheckoutUseCase;
import com.subprj.payment.domain.CheckoutResult;
import com.subprj.payment.adapter.common.IdempotencyCreator;
import com.subprj.payment.adapter.common.annotation.WebAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@WebAdapter
@Controller
public class CheckoutController {
    private final CheckoutUseCase checkoutUseCase;

    @GetMapping("/")
    public Mono<String> checkoutPage(Model model, CheckoutDto.CheckoutRequest request) {
        CheckoutCommand command = CheckoutCommand.builder()
                .cartId(request.getCartId())
                .buyerId(request.getBuyerId())
                .productIds(request.getProductIds())
                .idempotencyKey(IdempotencyCreator.create(request.getSeed()))
                .build();
        Mono<CheckoutResult> checkoutResult = checkoutUseCase.checkout(command);
        return checkoutResult.map(r -> {
            model.addAttribute("orderId", r.getOrderId());
            model.addAttribute("orderName", r.getOrderName());
            model.addAttribute("amount", r.getAmount());
            return "checkout";
        });
    }
}
