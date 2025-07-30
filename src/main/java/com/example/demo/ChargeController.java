package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class ChargeController {

    private final Map<String, String> idempotencyStore = new ConcurrentHashMap<>();

    @PostMapping("/v1/charge")
    public ResponseEntity<String> charge(@RequestBody ChargeRequest request) throws InterruptedException {
        Thread.sleep(7000);
        System.out.println("충전이 완료되었습니다. ");
        return ResponseEntity.ok("충전 성공 확인 응답");
    }

    @PostMapping("/v2/charge")
    public ResponseEntity<String> charge(
            @RequestBody ChargeRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) throws InterruptedException {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return ResponseEntity.badRequest().body("멱등성 키가 필요합니다.");
        }

        if (idempotencyStore.containsKey(idempotencyKey)) {
            System.out.println("중복 요청 감지!");
            return ResponseEntity.ok("이미 처리됨: " + idempotencyStore.get(idempotencyKey));
        }

        Thread.sleep(7000);
        System.out.println("충전이 성공되었습니다.");

        idempotencyStore.put(idempotencyKey, "충전 성공");

        return ResponseEntity.ok("충전 성공 응답 메시지");
    }
}
