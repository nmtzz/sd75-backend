package gadgetify.server.services;

import gadgetify.server.dtos.Response;
import gadgetify.server.entities.DailyOrdersOverview;
import gadgetify.server.entities.MonthlyAllOrder;
import gadgetify.server.entities.MonthlyDeliveredOrder;
import gadgetify.server.repositories.DailyOrdersOverviewRepository;
import gadgetify.server.repositories.MonthlyAllOrderRepository;
import gadgetify.server.repositories.MonthlyDeliveredOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final MonthlyAllOrderRepository monthlyAllOrderRepository;
    private final MonthlyDeliveredOrderRepository monthlyDeliveredOrderRepository;
    private final DailyOrdersOverviewRepository dailyOrdersOverviewRepository;

    public Response<List<MonthlyAllOrder>> getMonthlyAllOrders() {
        var monthlyAllOrders = monthlyAllOrderRepository.findAll();
        return Response.<List<MonthlyAllOrder>>builder()
                .result(monthlyAllOrders)
                .build();
    }

    public Response<List<MonthlyDeliveredOrder>> getMonthlyDeliveredOrders() {
        var monthlyDeliveredOrders = monthlyDeliveredOrderRepository.findAll();
        return Response.<List<MonthlyDeliveredOrder>>builder()
                .result(monthlyDeliveredOrders)
                .build();
    }
    public Response<List<DailyOrdersOverview>> getDailyOrdersOverview() {
        var dailyOrdersOverview = dailyOrdersOverviewRepository.findAll();
        return Response.<List<DailyOrdersOverview>>builder()
                .result(dailyOrdersOverview)
                .build();
    }
}
