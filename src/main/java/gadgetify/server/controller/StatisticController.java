package gadgetify.server.controller;

import gadgetify.server.entities.DailyOrdersOverview;
import gadgetify.server.services.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import gadgetify.server.dtos.Response;
import gadgetify.server.entities.MonthlyAllOrder;
import gadgetify.server.entities.MonthlyDeliveredOrder;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;
    @GetMapping("/monthly-all-orders")
    public Response<List<MonthlyAllOrder>> getMonthlyAllOrders() {
        return statisticService.getMonthlyAllOrders();
    }
    @GetMapping("/monthly-delivered-orders")
    public Response<List<MonthlyDeliveredOrder>> getMonthlyDeliveredOrders() {
        return statisticService.getMonthlyDeliveredOrders();
    }
    @GetMapping("/overview")
    public Response<List<DailyOrdersOverview>> getDailyOrdersOverview() {
        return statisticService.getDailyOrdersOverview();
    }

}
