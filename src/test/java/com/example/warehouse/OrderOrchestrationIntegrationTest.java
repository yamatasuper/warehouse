//package com.example.warehouse;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@AutoConfigureWireMock(port = 0)
//@DirtiesContext
//public class OrderOrchestrationIntegrationTest {
//
//    @Autowired
//    private OrderOrchestrationService orchestrationService;
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private KafkaTemplate<String, Object> kafkaTemplate;
//
//    @Test
//    public void testHappyPath() throws Exception {
//        // 1. Setup test data
//        OrderEntity order = createTestOrder();
//
//        // 2. Mock external services
//        stubFor(post(urlEqualTo("/api/contracts/register"))
//                .willReturn(aResponse()
//                        .withHeader("Content-Type", "application/json")
//                        .withBody("{\"contractId\": \"CONTRACT_123\"}")));
//
//        stubFor(post(urlEqualTo("/api/deliveries/register"))
//                .willReturn(aResponse()
//                        .withHeader("Content-Type", "application/json")
//                        .withBody("{\"deliveryDate\": \"2024-01-15T10:00:00Z\"}")));
//
//        stubFor(post(urlEqualTo("/api/payments/process"))
//                .willReturn(aResponse()
//                        .withHeader("Content-Type", "application/json")
//                        .withBody("{\"success\": true}")));
//
//        // 3. Start orchestration
//        OrderConfirmRequest request = new OrderConfirmRequest(
//                "1234567890",
//                "ACC123456",
//                "Test Address"
//        );
//
//        UUID businessKey = orchestrationService.startOrderConfirmationProcess(
//                order.getId(), request
//        );
//
//        // 4. Simulate compliance approval
//        ComplianceResponseMessage complianceResponse = new ComplianceResponseMessage();
//        complianceResponse.setBusinessKey(businessKey.toString());
//        complianceResponse.setApproved(true);
//
//        kafkaTemplate.send("compliance-check-responses", businessKey.toString(), complianceResponse);
//
//        // 5. Wait for process completion
//        await().atMost(30, TimeUnit.SECONDS)
//                .until(() -> orderRepository.findById(order.getId())
//                        .map(OrderEntity::getStatus)
//                        .orElse(null) == OrderStatus.CONFIRMED);
//
//        // 6. Verify results
//        OrderEntity updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
//        assertEquals(OrderStatus.CONFIRMED, updatedOrder.getStatus());
//        assertEquals("CONTRACT_123", updatedOrder.getContractId());
//        assertNotNull(updatedOrder.getDeliveryDate());
//
//        // Verify external calls
//        verify(postRequestedFor(urlEqualTo("/api/contracts/register")));
//        verify(postRequestedFor(urlEqualTo("/api/deliveries/register")));
//        verify(postRequestedFor(urlEqualTo("/api/payments/process")));
//    }
//
//    private OrderEntity createTestOrder() {
//        OrderEntity order = new OrderEntity();
//        order.setId(UUID.randomUUID());
//        order.setCustomerId(1L);
//        order.setStatus(OrderStatus.CREATED);
//        order.setDeliveryAddress("Test Address");
//        order.setTotalAmount(new BigDecimal("999.99"));
//        return orderRepository.save(order);
//    }
//}
