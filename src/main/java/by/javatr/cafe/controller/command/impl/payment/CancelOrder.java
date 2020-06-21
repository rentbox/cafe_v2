package by.javatr.cafe.controller.command.impl.payment;

import by.javatr.cafe.constant.PaymentStatus;
import by.javatr.cafe.constant.SessionAttributes;
import by.javatr.cafe.container.annotation.Autowired;
import by.javatr.cafe.container.annotation.Component;
import by.javatr.cafe.controller.command.Command;
import by.javatr.cafe.controller.content.Navigation;
import by.javatr.cafe.controller.content.RequestContent;
import by.javatr.cafe.controller.content.RequestResult;
import by.javatr.cafe.entity.Order;
import by.javatr.cafe.exception.DAOException;
import by.javatr.cafe.exception.ServiceException;
import by.javatr.cafe.service.impl.OrderService;

import javax.servlet.http.HttpServletResponse;

@Component
public class CancelOrder implements Command {

    @Autowired
    OrderService orderService;

    @Override
    public RequestResult execute(RequestContent content) throws ServiceException {

        int order_id = Integer.parseInt(content.getRequestParam("order_id"));

        int user_id =(int) content.getSessionAttr(SessionAttributes.USER_ID);

        Order order = new Order(order_id, user_id);

        order = orderService.getOrder(order);

        order.setStatus(PaymentStatus.CANCELED);

        orderService.updateOrder(order);

        return new RequestResult(Navigation.REDIRECT, "/checkout/" + order.getOrder_id(), HttpServletResponse.SC_OK);
    }
}