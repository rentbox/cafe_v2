package by.javatr.cafe.controller.command.impl.payment;

import by.javatr.cafe.constant.PaymentMethod;
import by.javatr.cafe.constant.PaymentStatus;
import by.javatr.cafe.constant.RequestParameters;
import by.javatr.cafe.constant.SessionAttributes;
import by.javatr.cafe.container.annotation.Autowired;
import by.javatr.cafe.container.annotation.Component;
import by.javatr.cafe.controller.command.Command;
import by.javatr.cafe.controller.content.Navigation;
import by.javatr.cafe.controller.content.RequestContent;
import by.javatr.cafe.controller.content.RequestResult;
import by.javatr.cafe.entity.*;
import by.javatr.cafe.exception.ServiceException;
import by.javatr.cafe.service.ICartService;
import by.javatr.cafe.service.IOrderService;
import by.javatr.cafe.service.IUserService;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static by.javatr.cafe.util.Utils.countSame;

/**
 * Class for processing payment request.
 * Make an order with credit
 */
@Component
public class CreditOrder implements Command {

    @Autowired
    IUserService userService;
    @Autowired
    IOrderService orderService;
    @Autowired
    ICartService cartService;

    @Override
    public RequestResult execute(RequestContent content) throws ServiceException {

        String addressIdStr = content.getRequestParam(RequestParameters.ADDRESS);
        int addressId = Integer.parseInt(addressIdStr);

        User user = userService.find((int)content.getSessionAttr(SessionAttributes.USER_ID));

        Cart cart = (Cart) content.getSessionAttr(SessionAttributes.CART);

        List<Address> address1 = user.getAddress();

        Address userAddress = null;

        for (Address addr : address1) {
            if (addr.getId() == addressId) {
                userAddress = addr;
                break;
            }
        }

        if(userAddress == null){
            return new RequestResult(HttpServletResponse.SC_BAD_REQUEST);
        }

        BigDecimal amount = cartService.amount(cart);
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal money = user.getMoney();
        money = money.setScale(2, RoundingMode.HALF_UP);


        DateFormat createData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = createData.format(Calendar.getInstance().getTime());


        Calendar format = Calendar.getInstance();
        format.add(Calendar.MONTH, 2);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String creditTime = dateFormat.format(format.getTime());


        final Map<Dish, Integer> dishes = countSame(cart.getUserCart());

        Order order = new Order(PaymentMethod.CREDIT, dishes, createTime, creditTime, userAddress, amount, PaymentStatus.PAID, user.getId());

        if (money.compareTo(amount) >= 0) {
            order = orderService.creditOrder(order, user);
            if(order == null){
                return new RequestResult(HttpServletResponse.SC_BAD_REQUEST);
            }
            cartService.clean(cart);
        }
        else {
            return new RequestResult(HttpServletResponse.SC_BAD_REQUEST);
        }
        return new RequestResult(Navigation.REDIRECT, "/checkout/" + order.getOrderId(), HttpServletResponse.SC_FOUND);
    }


    private CreditOrder() {
    }
}
